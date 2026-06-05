package io.teknek.gossip.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PerNodeDataBulkMessage extends Base {
  private List<PerNodeDataMessage> messages = new ArrayList<>();

  public void addMessage(PerNodeDataMessage msg) {
    messages.add(msg);
  }

  public List<PerNodeDataMessage> getMessages() {
    return messages;
  }

  @Override public String toString() {
    return "GossipDataBulkMessage[" + messages.stream().map(Object::toString)
            .collect(Collectors.joining(",")) + "]";
  }
}
