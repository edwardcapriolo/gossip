package io.teknek.gossip.manager.handlers;

import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.Base;
import io.teknek.gossip.udp.UdpPerNodeDataMessage;

public class PerNodeDataMessageHandler implements MessageHandler {

  /**
   * @param gossipCore context.
   * @param gossipManager context.
   * @param base message reference.
   * @return boolean indicating success.
   */
  @Override
  public boolean invoke(GossipCore gossipCore, GossipManager gossipManager, Base base) {
    UdpPerNodeDataMessage message = (UdpPerNodeDataMessage) base;
    gossipCore.addPerNodeData(message);
    return true;
  }
}
