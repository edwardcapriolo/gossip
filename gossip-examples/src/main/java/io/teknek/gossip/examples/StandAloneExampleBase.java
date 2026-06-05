package io.teknek.gossip.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import io.teknek.gossip.GossipSettings;
import io.teknek.gossip.LocalMember;
import io.teknek.gossip.RemoteMember;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;

abstract class StandAloneExampleBase {
  private String lastInput = "{none}";

  private boolean clearTerminalScreen = true;

  private GossipManager gossipService = null;

  abstract void printValues(GossipManager gossipService);

  boolean processReadLoopInput(String line) {
    return true;
  }

  void exec(boolean willRead) throws IOException {
    gossipService.init();
    startMonitorLoop(gossipService);
    if (willRead) {
      startBlockingReadLoop();
    }
  }

  /*
   * Look for -s in args. If there, suppress terminal-clear on write results: shift args for
   * positional args, if necessary
   */
  String[] checkArgsForClearFlag(String[] args) {
    int pos = 0;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-s")) {
        clearTerminalScreen = false;
      } else {
        // in the case of the -s flag, shift args
        // down by one slot; this will end up with
        // a duplicate entry in the last position of args,
        // but this is ok, because it will be ignored
        args[pos++] = args[i];
      }
    }
    return args;
  }

  private void optionallyClearTerminal() {
    if (clearTerminalScreen) {
      System.out.print("\033[H\033[2J");
      System.out.flush();
    }
  }

  private void setLastInput(String input, boolean valid) {
    lastInput = input;
    if (!valid) {
      lastInput += " (invalid)";
    }
  }

  String getLastInput() {
    return lastInput;
  }

  private void startMonitorLoop(GossipManager gossipService) {
    new Thread(() -> {
      while (true) {
        optionallyClearTerminal();
        printLiveMembers(gossipService);
        printDeadMambers(gossipService);
        printValues(gossipService);
        try {
          Thread.sleep(2000);
        } catch (Exception ignore) {
        }
      }
    }).start();
  }

  private void printLiveMembers(GossipManager gossipService) {
    List<LocalMember> members = gossipService.getLiveMembers();
    if (members.isEmpty()) {
      System.out.println("Live: (none)");
      return;
    }
    System.out.println("Live: " + members.get(0));
    for (int i = 1; i < members.size(); i++) {
      System.out.println("    : " + members.get(i));
    }
  }

  private void printDeadMambers(GossipManager gossipService) {
    List<LocalMember> members = gossipService.getDeadMembers();
    if (members.isEmpty()) {
      System.out.println("Dead: (none)");
      return;
    }
    System.out.println("Dead: " + members.get(0));
    for (int i = 1; i < members.size(); i++) {
      System.out.println("    : " + members.get(i));
    }
  }

  private void startBlockingReadLoop() throws IOException {
    String line;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      while ((line = br.readLine()) != null) {
        System.out.println(line);
        boolean valid = processReadLoopInput(line);
        setLastInput(line, valid);
      }
    }
  }

  void initGossipManager(String[] args) {
    GossipSettings s = new GossipSettings();
    s.setWindowSize(1000);
    s.setGossipInterval(100);
    GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster("mycluster")
            .uri(URI.create(args[0])).id(args[1])
            .gossipMembers(Collections
                    .singletonList(new RemoteMember("mycluster", URI.create(args[2]), args[3])))
            .gossipSettings(s).build();
    setGossipService(gossipService);
  }

  void setGossipService(GossipManager gossipService) {
    this.gossipService = gossipService;
  }

  GossipManager getGossipManager() {
    return this.gossipService;
  }

}
