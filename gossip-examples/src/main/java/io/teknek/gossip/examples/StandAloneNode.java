package io.teknek.gossip.examples;

import java.io.IOException;

import io.teknek.gossip.manager.GossipManager;

public class StandAloneNode extends StandAloneExampleBase {

  private static boolean WILL_READ = false;

  public static void main(String[] args) throws InterruptedException, IOException {
    StandAloneNode example = new StandAloneNode(args);
    example.exec(WILL_READ);
  }

  StandAloneNode(String[] args) {
    args = super.checkArgsForClearFlag(args);
    super.initGossipManager(args);
  }

  @Override
  void printValues(GossipManager gossipService) {
  }

}
