package io.teknek.gossip.event;

import io.teknek.gossip.Member;

public interface GossipListener {
  void gossipEvent(Member member, GossipState state);
}
