package io.teknek.gossip.examples;

import java.io.IOException;

import io.teknek.gossip.crdt.PNCounter;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.SharedDataMessage;

public class StandAlonePNCounter extends StandAloneExampleBase {

  public static void main(String[] args) throws InterruptedException, IOException {
    StandAlonePNCounter example = new StandAlonePNCounter(args);
    boolean willRead = true;
    example.exec(willRead);
  }

  StandAlonePNCounter(String[] args) {
    args = super.checkArgsForClearFlag(args);
    super.initGossipManager(args);
  }

  void printValues(GossipManager gossipService) {
    System.out.println("Last Input: " + getLastInput());
    System.out.println("---------- " + (gossipService.findCrdt("myPNCounter") == null ? ""
            : gossipService.findCrdt("myPNCounter").value()));
    System.out.println("********** " + gossipService.findCrdt("myPNCounter"));
  }

  boolean processReadLoopInput(String line) {
    char op = line.charAt(0);
    char blank = line.charAt(1);
    String val = line.substring(2);
    Long l = null;
    boolean valid = true;
    try {
      l = Long.valueOf(val);
    } catch (NumberFormatException ex) {
      valid = false;
    }
    valid = valid && ((blank == ' ') && ((op == 'i') || (op == 'd')));
    if (valid) {
      if (op == 'i') {
        increment(l, getGossipManager());
      } else if (op == 'd') {
        decrement(l, getGossipManager());
      }
    }
    return valid;
  }

  void increment(Long l, GossipManager gossipManager) {
    PNCounter c = (PNCounter) gossipManager.findCrdt("myPNCounter");
    if (c == null) {
      c = new PNCounter(new PNCounter.Builder(gossipManager).increment((l)));
    } else {
      c = new PNCounter(c, new PNCounter.Builder(gossipManager).increment((l)));
    }
    SharedDataMessage m = new SharedDataMessage();
    m.setExpireAt(Long.MAX_VALUE);
    m.setKey("myPNCounter");
    m.setPayload(c);
    m.setTimestamp(System.currentTimeMillis());
    gossipManager.merge(m);
  }

  void decrement(Long l, GossipManager gossipManager) {
    PNCounter c = (PNCounter) gossipManager.findCrdt("myPNCounter");
    if (c == null) {
      c = new PNCounter(new PNCounter.Builder(gossipManager).decrement((l)));
    } else {
      c = new PNCounter(c, new PNCounter.Builder(gossipManager).decrement((l)));
    }
    SharedDataMessage m = new SharedDataMessage();
    m.setExpireAt(Long.MAX_VALUE);
    m.setKey("myPNCounter");
    m.setPayload(c);
    m.setTimestamp(System.currentTimeMillis());
    gossipManager.merge(m);
  }

}
