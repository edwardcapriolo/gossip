package io.teknek.gossip;

import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeedMemberComesLiveTest {

  @Test
  public void seedMemberComesLiveAfterDirectGossipContact() throws Exception {
    String cluster = "seed-live-" + UUID.randomUUID();
    int basePort = 35_000 + Math.floorMod(cluster.hashCode(), 1_000);
    URI seedUri = URI.create("udp://127.0.0.1:" + basePort);
    URI node1Uri = URI.create("udp://127.0.0.1:" + (basePort + 1));
    URI node2Uri = URI.create("udp://127.0.0.1:" + (basePort + 2));
    RemoteMember seedMember = new RemoteMember(cluster, seedUri, "seed");

    GossipSettings settings = new GossipSettings();
    settings.setPersistRingState(false);
    settings.setPersistDataState(false);
    settings.setGossipInterval(100);
    settings.setCleanupInterval(10_000);
    settings.setMinimumSamples(100_000);

    GossipManager seed = manager(cluster, "seed", seedUri, List.of(), settings);
    GossipManager node1 = manager(cluster, "node-1", node1Uri, List.of(seedMember), settings);
    GossipManager node2 = manager(cluster, "node-2", node2Uri, List.of(seedMember), settings);
    try {
      seed.init();
      node1.init();
      node2.init();

      await().atMost(Duration.ofSeconds(10)).until(() -> hasLive(seed, "node-1") && hasLive(seed, "node-2"));
      await().atMost(Duration.ofSeconds(10)).until(() -> hasLive(node1, "seed"));
      await().atMost(Duration.ofSeconds(10)).until(() -> hasLive(node2, "seed"));

      assertTrue(hasLive(node1, "seed"));
      assertTrue(hasLive(node2, "seed"));
    } finally {
      node2.shutdown();
      node1.shutdown();
      seed.shutdown();
    }
  }

  private static GossipManager manager(String cluster, String id, URI uri, List<Member> seeds, GossipSettings settings) {
    return GossipManagerBuilder.newBuilder()
            .cluster(cluster)
            .id(id)
            .uri(uri)
            .gossipMembers(seeds)
            .gossipSettings(settings)
            .build();
  }

  private static boolean hasLive(GossipManager manager, String id) {
    return manager.getLiveMembers().stream().anyMatch(member -> member.getId().equals(id));
  }

}
