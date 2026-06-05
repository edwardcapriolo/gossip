package io.teknek.gossip.manager.handlers;

import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.Base;
import io.teknek.gossip.udp.Trackable;

public class ResponseHandler implements MessageHandler {
  
  /**
   * @param gossipCore context.
   * @param gossipManager context.
   * @param base message reference.
   * @return boolean indicating success.
   */
  @Override
  public boolean invoke(GossipCore gossipCore, GossipManager gossipManager, Base base) {
    if (base instanceof Trackable) {
      Trackable t = (Trackable) base;
      gossipCore.handleResponse(t.getUuid() + "/" + t.getUriFrom(), (Base) t);
      return true;
    }
    return false;
  }
}
