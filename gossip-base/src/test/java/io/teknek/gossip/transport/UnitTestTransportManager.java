
package io.teknek.gossip.transport;

import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/** Only use in unit tests! */
public class UnitTestTransportManager extends AbstractTransportManager { 
  
  private static final Map<URI, UnitTestTransportManager> allManagers = new ConcurrentHashMap<>();
  
  private final URI localEndpoint;
  private BlockingQueue<byte[]> buffers = new ArrayBlockingQueue<byte[]>(1000);
  
  public UnitTestTransportManager(GossipManager gossipManager, GossipCore gossipCore) {
    super(gossipManager, gossipCore);
    localEndpoint = gossipManager.getMyself().getUri();
  }

  @Override
  public void send(URI endpoint, byte[] buf) throws IOException {
    if (allManagers.containsKey(endpoint)) {
      try {
        allManagers.get(endpoint).buffers.put(buf);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  @Override
  public byte[] read() throws IOException {
    try {
      return buffers.take();
    } catch (InterruptedException ex) {
      // probably not the right thing to do, but we'll see.
      throw new IOException(ex);
    }
  }

  @Override
  public void shutdown() {
    allManagers.remove(localEndpoint);
    super.shutdown();
  }

  @Override
  public void startEndpoint() {
    allManagers.put(localEndpoint, this);
  }
}
