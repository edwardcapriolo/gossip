package io.teknek.gossip.event;

public enum GossipState {
  UP("up"), DOWN("down");
  @SuppressWarnings("unused")
  private final String state;

  private GossipState(String state) {
    this.state = state;
  }
}
