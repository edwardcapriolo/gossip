package io.teknek.gossip;

import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SameIdNewUriReplacesOldMemberTest {

  @Test
  public void sameIdWithNewUriReplacesOldMemberAddress() throws Exception {
    String cluster = "same-id-new-uri-" + UUID.randomUUID();
    int basePort = 36_000 + Math.floorMod(cluster.hashCode(), 1_000);
    URI seedUri = URI.create("udp://127.0.0.1:" + basePort);
    URI oldWorkerUri = URI.create("udp://127.0.0.1:" + (basePort + 1));
    URI newWorkerUri = URI.create("udp://127.0.0.1:" + (basePort + 2));

    GossipSettings settings = new GossipSettings();
    settings.setPersistRingState(false);
    settings.setPersistDataState(false);
    settings.setGossipInterval(100);
    settings.setCleanupInterval(10_000);

    GossipManager seed = manager(cluster, "seed", seedUri, List.of(), settings);
    GossipManager worker = manager(cluster, "worker", oldWorkerUri,
            List.of(new RemoteMember(cluster, seedUri, "seed")), settings);
    try {
      seed.init();
      worker.init();
      await().atMost(Duration.ofSeconds(10)).until(() -> liveUri(seed, "worker").equals(oldWorkerUri));
    } finally {
      worker.shutdown();
    }

    GossipManager restartedWorker = manager(cluster, "worker", newWorkerUri,
            List.of(new RemoteMember(cluster, seedUri, "seed")), settings);
    try {
      restartedWorker.init();
      await().atMost(Duration.ofSeconds(10)).until(() -> liveUri(seed, "worker").equals(newWorkerUri));
      assertEquals(List.of(newWorkerUri), seed.getLiveMembers().stream()
              .filter(member -> member.getId().equals("worker"))
              .map(Member::getUri)
              .toList());
      assertEquals(List.of(), seed.getDeadMembers().stream()
              .filter(member -> member.getId().equals("worker"))
              .map(Member::getUri)
              .toList());
    } finally {
      restartedWorker.shutdown();
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

  private static URI liveUri(GossipManager manager, String id) {
    return manager.getLiveMembers().stream()
            .filter(member -> member.getId().equals(id))
            .map(Member::getUri)
            .findFirst()
            .orElse(URI.create("udp://missing:1"));
  }

}
