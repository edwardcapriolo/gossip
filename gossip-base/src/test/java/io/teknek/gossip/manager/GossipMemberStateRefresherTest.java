package io.teknek.gossip.manager;

import io.teknek.gossip.GossipSettings;
import io.teknek.gossip.LocalMember;
import io.teknek.gossip.event.GossipState;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GossipMemberStateRefresherTest {

  @Test
  public void cleanupFallbackComparesMillisecondHeartbeatToMillisecondDeadline() throws Exception {
    GossipSettings settings = new GossipSettings();
    settings.setCleanupInterval(1_000);
    GossipMemberStateRefresher refresher = new GossipMemberStateRefresher(new HashMap<>(), settings,
            (member, state) -> {}, (nodeId, key) -> null);
    long oldHeartbeat = System.currentTimeMillis() - 2_000;
    LocalMember member = new LocalMember("cluster", new URI("udp://127.0.0.1:5001"), "node-1",
            oldHeartbeat, Map.of(), settings.getWindowSize(), settings.getMinimumSamples(), settings.getDistribution());

    assertEquals(GossipState.DOWN, refresher.calcRequiredStateCleanupInterval(member, GossipState.UP));
  }
}
