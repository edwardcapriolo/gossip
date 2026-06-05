package io.teknek.gossip.manager.handlers;

import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.Base;

public interface MessageHandler {
  /**
   * @param gossipCore context.
   * @param gossipManager context.
   * @param base message reference.
   * @return boolean indicating success.
   */
  boolean invoke(GossipCore gossipCore, GossipManager gossipManager, Base base);
}
