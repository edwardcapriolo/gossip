package io.teknek.gossip.spring;

import io.teknek.gossip.LocalMember;
import io.teknek.gossip.manager.GossipManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GossipStatusController {

  private final GossipManager gossipManager;

  public GossipStatusController(GossipManager gossipManager) {
    this.gossipManager = gossipManager;
  }

  @GetMapping("/gossip/status")
  public Map<String, Object> status() {
    return Map.of(
            "cluster", gossipManager.getMyself().getClusterName(),
            "self", member(gossipManager.getMyself()),
            "live", members(gossipManager.getLiveMembers()),
            "dead", members(gossipManager.getDeadMembers()));
  }

  private List<Map<String, Object>> members(List<LocalMember> members) {
    return members.stream().map(this::member).toList();
  }

  private Map<String, Object> member(LocalMember member) {
    return Map.of(
            "id", member.getId(),
            "uri", member.getUri().toASCIIString(),
            "heartbeat", member.getHeartbeat(),
            "properties", member.getProperties());
  }
}
