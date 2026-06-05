package io.teknek.gossip.manager.handlers;

import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.Base;
import io.teknek.gossip.model.PerNodeDataMessage;
import io.teknek.gossip.model.ShutdownMessage;

public class ShutdownMessageHandler implements MessageHandler {
  
  /**
   * @param gossipCore context.
   * @param gossipManager context.
   * @param base message reference.
   * @return boolean indicating success.
   */
  @Override
  public boolean invoke(GossipCore gossipCore, GossipManager gossipManager, Base base) {
    ShutdownMessage s = (ShutdownMessage) base;
    PerNodeDataMessage m = new PerNodeDataMessage();
    m.setKey(ShutdownMessage.PER_NODE_KEY);
    m.setNodeId(s.getNodeId());
    m.setPayload(base);
    m.setTimestamp(System.currentTimeMillis());
    m.setExpireAt(System.currentTimeMillis() + 30L * 1000L);
    gossipCore.addPerNodeData(m);
    return true;
  }
}
