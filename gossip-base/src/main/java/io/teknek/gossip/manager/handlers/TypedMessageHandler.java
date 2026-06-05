package io.teknek.gossip.manager.handlers;

import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.Base;

public class TypedMessageHandler implements MessageHandler {
  final private Class<?> messageClass;
  final private MessageHandler messageHandler;

  public TypedMessageHandler(Class<?> messageClass, MessageHandler messageHandler) {
    if (messageClass == null || messageHandler == null) {
      throw new NullPointerException();
    }
    this.messageClass = messageClass;
    this.messageHandler = messageHandler;
  }

  /**
   * @param gossipCore context.
   * @param gossipManager context.
   * @param base message reference.
   * @return true if types match, false otherwise.
   */
  @Override
  public boolean invoke(GossipCore gossipCore, GossipManager gossipManager, Base base) {
    if (messageClass.isAssignableFrom(base.getClass())) {
      messageHandler.invoke(gossipCore, gossipManager, base);
      return true;
    } else {
      return false;
    }
  }
}
