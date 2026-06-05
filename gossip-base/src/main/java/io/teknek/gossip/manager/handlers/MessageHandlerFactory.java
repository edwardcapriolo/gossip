
package io.teknek.gossip.manager.handlers;

import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.*;

import java.util.Arrays;

public class MessageHandlerFactory {

  public static MessageHandler defaultHandler() {
    return concurrentHandler(
        new TypedMessageHandler(Response.class, new ResponseHandler()),
        new TypedMessageHandler(ShutdownMessage.class, new ShutdownMessageHandler()),
        new TypedMessageHandler(PerNodeDataMessage.class, new PerNodeDataMessageHandler()),
        new TypedMessageHandler(SharedDataMessage.class, new SharedDataMessageHandler()),
        new TypedMessageHandler(ActiveGossipMessage.class, new ActiveGossipMessageHandler()),
        new TypedMessageHandler(PerNodeDataBulkMessage.class, new PerNodeDataBulkMessageHandler()),
        new TypedMessageHandler(SharedDataBulkMessage.class, new SharedDataBulkMessageHandler())
    );
  }

  public static MessageHandler concurrentHandler(MessageHandler... handlers) {
    if (handlers == null)
      throw new NullPointerException("handlers cannot be null");
    if (Arrays.asList(handlers).stream().filter(i -> i != null).count() != handlers.length) {
      throw new NullPointerException("found at least one null handler");
    }
    return new MessageHandler() {
      @Override public boolean invoke(GossipCore gossipCore, GossipManager gossipManager,
              Base base) {
        // return true if at least one of the component handlers return true.
        return Arrays.asList(handlers).stream()
                .filter((mi) -> mi.invoke(gossipCore, gossipManager, base)).count() > 0;
      }
    };
  }
}

