package io.teknek.gossip.spring;

import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.transport.AbstractTransportManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpTransportManager extends AbstractTransportManager {

  static final String MESSAGE_PATH = "/gossip/message";

  private final HttpClient httpClient;
  private final Duration requestTimeout;
  private final AtomicBoolean springReady = new AtomicBoolean(false);
  private final AtomicBoolean activeStarted = new AtomicBoolean(false);

  public HttpTransportManager(GossipManager gossipManager, GossipCore gossipCore) {
    super(gossipManager, gossipCore);
    this.httpClient = HttpClient.newHttpClient();
    this.requestTimeout = Duration.ofMillis(Math.max(1_000, gossipManager.getSettings().getGossipInterval() * 2L));
  }

  @Override
  public void startEndpoint() {
    // Spring MVC owns the passive endpoint for this transport.
  }

  @Override
  public void startActiveGossiper() {
    if (springReady.get() && activeStarted.compareAndSet(false, true)) {
      super.startActiveGossiper();
    }
  }

  void startAfterSpringReady() {
    springReady.set(true);
    startActiveGossiper();
  }

  @Override
  public void send(URI endpoint, byte[] buf) throws IOException {
    HttpRequest request = HttpRequest.newBuilder(messageUri(endpoint))
            .timeout(requestTimeout)
            .header("Content-Type", "application/octet-stream")
            .POST(HttpRequest.BodyPublishers.ofByteArray(buf))
            .build();
    try {
      HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        throw new IOException("HTTP gossip send failed status=" + response.statusCode()
                + " endpoint=" + endpoint);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted sending HTTP gossip message to " + endpoint, e);
    }
  }

  @Override
  public byte[] read() throws IOException {
    throw new IOException("HTTP gossip messages are read by the Spring controller");
  }

  private URI messageUri(URI endpoint) throws IOException {
    String path = endpoint.getPath();
    if (path == null || path.isBlank() || "/".equals(path)) {
      path = MESSAGE_PATH;
    } else {
      path = path.endsWith("/") ? path.substring(0, path.length() - 1) + MESSAGE_PATH : path + MESSAGE_PATH;
    }
    try {
      return new URI(endpoint.getScheme(), endpoint.getUserInfo(), endpoint.getHost(), endpoint.getPort(),
              path, null, null);
    } catch (URISyntaxException e) {
      throw new IOException("Invalid HTTP gossip endpoint " + endpoint, e);
    }
  }
}
