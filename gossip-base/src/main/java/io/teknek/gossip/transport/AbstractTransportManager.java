package io.teknek.gossip.transport;

import com.codahale.metrics.MetricRegistry;
import io.teknek.gossip.manager.AbstractActiveGossiper;
import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manage the protcol threads (active and passive gossipers).
 */
public abstract class AbstractTransportManager implements TransportManager {
  
  public static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransportManager.class);
  
  private final ExecutorService gossipThreadExecutor;
  private final AbstractActiveGossiper activeGossipThread;
  protected final GossipManager gossipManager;
  protected final GossipCore gossipCore;
  
  public AbstractTransportManager(GossipManager gossipManager, GossipCore gossipCore) {
    this.gossipManager = gossipManager;
    this.gossipCore = gossipCore;
    gossipThreadExecutor = Executors.newCachedThreadPool();
    activeGossipThread = ReflectionUtils.constructWithReflection(
      gossipManager.getSettings().getActiveGossipClass(),
        new Class<?>[]{
            GossipManager.class, GossipCore.class, MetricRegistry.class
        },
        new Object[]{
            gossipManager, gossipCore, gossipManager.getRegistry()
        });
  }

  // shut down threads etc.
  @Override
  public void shutdown() {
    gossipThreadExecutor.shutdown();
    if (activeGossipThread != null) {
      activeGossipThread.shutdown();
    }
    try {
      boolean result = gossipThreadExecutor.awaitTermination(10, TimeUnit.MILLISECONDS);
      if (!result) {
        // common when blocking patterns are used to read data from a socket.
        LOGGER.warn("executor shutdown timed out");
      }
    } catch (InterruptedException e) {
      LOGGER.error("Interrupted while shutting down transport", e);
    }
    gossipThreadExecutor.shutdownNow();
  }

  @Override
  public void startActiveGossiper() {
    activeGossipThread.init();
  }

  @Override
  public abstract void startEndpoint();
}
