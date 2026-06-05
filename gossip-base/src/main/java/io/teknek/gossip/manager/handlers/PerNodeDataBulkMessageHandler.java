package io.teknek.gossip.manager.handlers;

import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.Base;
import io.teknek.gossip.model.PerNodeDataMessage;
import io.teknek.gossip.udp.UdpPerNodeDataBulkMessage;

public class PerNodeDataBulkMessageHandler implements MessageHandler {
  
  /**
   * @param gossipCore context.
   * @param gossipManager context.
   * @param base message reference.
   * @return boolean indicating success.
   */
  @Override
  public boolean invoke(GossipCore gossipCore, GossipManager gossipManager, Base base) {
    UdpPerNodeDataBulkMessage udpMessage = (UdpPerNodeDataBulkMessage) base;
    for (PerNodeDataMessage dataMsg: udpMessage.getMessages())
      gossipCore.addPerNodeData(dataMsg);
    return true;
  }
}
