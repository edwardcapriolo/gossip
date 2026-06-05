package io.teknek.gossip.examples;

import java.io.IOException;

import io.teknek.gossip.crdt.GrowOnlyCounter;
import io.teknek.gossip.crdt.OrSet;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.SharedDataMessage;

public class StandAloneNodeCrdtOrSet extends StandAloneExampleBase {

  private static final String INDEX_KEY_FOR_SET = "abc";

  private static final String INDEX_KEY_FOR_COUNTER = "def";

  public static void main(String[] args) throws InterruptedException, IOException {
    StandAloneNodeCrdtOrSet example = new StandAloneNodeCrdtOrSet(args);
    boolean willRead = true;
    example.exec(willRead);
  }

  StandAloneNodeCrdtOrSet(String[] args) {
    args = super.checkArgsForClearFlag(args);
    super.initGossipManager(args);
  }

  void printValues(GossipManager gossipService) {
    System.out.println("Last Input: " + getLastInput());
    System.out.println("---------- Or Set " + (gossipService.findCrdt(INDEX_KEY_FOR_SET) == null
            ? "" : gossipService.findCrdt(INDEX_KEY_FOR_SET).value()));
    System.out.println("********** " + gossipService.findCrdt(INDEX_KEY_FOR_SET));
    System.out.println(
            "^^^^^^^^^^ Grow Only Counter" + (gossipService.findCrdt(INDEX_KEY_FOR_COUNTER) == null
                    ? "" : gossipService.findCrdt(INDEX_KEY_FOR_COUNTER).value()));
    System.out.println("$$$$$$$$$$ " + gossipService.findCrdt(INDEX_KEY_FOR_COUNTER));
  }

  boolean processReadLoopInput(String line) {
    boolean valid = true;
    char op = line.charAt(0);
    String val = line.substring(2);
    if (op == 'a') {
      addData(val, getGossipManager());
    } else if (op == 'r') {
      removeData(val, getGossipManager());
    } else if (op == 'g') {
      if (isNonNegativeNumber(val)) {
        gcount(val, getGossipManager());
      } else {
        valid = false;
      }
    } else if (op == 'l') {
      if ((val == INDEX_KEY_FOR_SET) || (val == INDEX_KEY_FOR_COUNTER)) {
        listen(val, getGossipManager());
      } else {
        valid = false;
      }
    } else {
      valid = false;
    }
    return valid;
  }

  private boolean isNonNegativeNumber(String val) {
    long l = 0;
    try {
      Long n = Long.parseLong(val);
      l = n.longValue();
    } catch (Exception e) {
      return false;
    }
    return (l >= 0);
  }

  private static void listen(String val, GossipManager gossipManager) {
    gossipManager.registerSharedDataSubscriber((key, oldValue, newValue) -> {
      if (key.equals(val)) {
        System.out.println(
                "Event Handler fired for key = '" + key + "'! " + oldValue + " " + newValue);
      }
    });
  }

  private static void gcount(String val, GossipManager gossipManager) {
    GrowOnlyCounter c = (GrowOnlyCounter) gossipManager.findCrdt(INDEX_KEY_FOR_COUNTER);
    Long l = Long.valueOf(val);
    if (c == null) {
      c = new GrowOnlyCounter(new GrowOnlyCounter.Builder(gossipManager).increment((l)));
    } else {
      c = new GrowOnlyCounter(c, new GrowOnlyCounter.Builder(gossipManager).increment((l)));
    }
    SharedDataMessage m = new SharedDataMessage();
    m.setExpireAt(Long.MAX_VALUE);
    m.setKey(INDEX_KEY_FOR_COUNTER);
    m.setPayload(c);
    m.setTimestamp(System.currentTimeMillis());
    gossipManager.merge(m);
  }

  private static void removeData(String val, GossipManager gossipService) {
    @SuppressWarnings("unchecked")
    OrSet<String> s = (OrSet<String>) gossipService.findCrdt(INDEX_KEY_FOR_SET);
    SharedDataMessage m = new SharedDataMessage();
    m.setExpireAt(Long.MAX_VALUE);
    m.setKey(INDEX_KEY_FOR_SET);
    m.setPayload(new OrSet<String>(s, new OrSet.Builder<String>().remove(val)));
    m.setTimestamp(System.currentTimeMillis());
    gossipService.merge(m);
  }

  private static void addData(String val, GossipManager gossipService) {
    SharedDataMessage m = new SharedDataMessage();
    m.setExpireAt(Long.MAX_VALUE);
    m.setKey(INDEX_KEY_FOR_SET);
    m.setPayload(new OrSet<String>(val));
    m.setTimestamp(System.currentTimeMillis());
    gossipService.merge(m);
  }

}
