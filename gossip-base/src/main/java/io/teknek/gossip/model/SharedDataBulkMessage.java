package io.teknek.gossip.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SharedDataBulkMessage extends Base {
  private List<SharedDataMessage> messages = new ArrayList<>();

  public void addMessage(SharedDataMessage msg) {
    messages.add(msg);
  }

  public List<SharedDataMessage> getMessages() {
    return messages;
  }

  @Override public String toString() {
    return "SharedGossipDataBulkMessage[" + messages.stream().map(Object::toString)
            .collect(Collectors.joining(",")) + "]";
  }
}
