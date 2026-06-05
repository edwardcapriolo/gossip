package io.teknek.gossip.manager;

import com.codahale.metrics.MetricRegistry;
import io.teknek.gossip.Member;
import io.teknek.gossip.GossipSettings;
import io.teknek.gossip.LocalMember;
import io.teknek.gossip.manager.handlers.MessageHandler;
import io.teknek.gossip.manager.handlers.ResponseHandler;
import io.teknek.gossip.manager.handlers.TypedMessageHandler;
import io.teknek.gossip.model.Response;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GossipManagerBuilderTest {

  private GossipManagerBuilder.ManagerBuilder builder;
  
  @BeforeEach
  public void setup() throws Exception {
    builder = GossipManagerBuilder.newBuilder()
        .id("id")
        .cluster("aCluster")
        .uri(new URI("udp://localhost:2000"))
        .gossipSettings(new GossipSettings());
  }
  
  @Test
  public void idShouldNotBeNull() {
    assertThrows(IllegalArgumentException.class,() -> {
        GossipManagerBuilder.newBuilder().cluster("aCluster").build();
    });
  }

  @Test
  public void clusterShouldNotBeNull() {
      assertThrows(IllegalArgumentException.class,() -> {
          GossipManagerBuilder.newBuilder().id("id").build();
      });
  }

  @Test
  public void settingsShouldNotBeNull() {
      assertThrows(IllegalArgumentException.class,() -> {
          GossipManagerBuilder.newBuilder().id("id").cluster("aCluster").build();
      });
  }
  
  @Test
  public void createMembersListIfNull() throws URISyntaxException {
    GossipManager gossipManager = builder.gossipMembers(null).registry(new MetricRegistry()).build();
    assertNotNull(gossipManager.getLiveMembers());
  }

  @Test
  public void createDefaultMessageHandlerIfNull() throws URISyntaxException {
    GossipManager gossipManager = builder.messageHandler(null).registry(new MetricRegistry()).build();
    assertNotNull(gossipManager.getMessageHandler());
  }

  @Test
  public void testMessageHandlerKeeping() throws URISyntaxException {
    MessageHandler mi = new TypedMessageHandler(Response.class, new ResponseHandler());
    GossipManager gossipManager = builder.messageHandler(mi).registry(new MetricRegistry()).build();
    assertNotNull(gossipManager.getMessageHandler());
    Assert.assertEquals(gossipManager.getMessageHandler(), mi);
  }

  @Test
  public void useMemberListIfProvided() throws URISyntaxException {
    LocalMember member = new LocalMember(
            "aCluster", new URI("udp://localhost:2000"), "aGossipMember",
            System.nanoTime(), new HashMap<String, String>(), 1000, 1, "exponential");
    List<Member> memberList = new ArrayList<>();
    memberList.add(member);
    GossipManager gossipManager = builder
        .uri(new URI("udp://localhost:8000"))
        .gossipMembers(memberList).registry(new MetricRegistry()).build();
    assertEquals(1, gossipManager.getDeadMembers().size());
    assertEquals(member.getId(), gossipManager.getDeadMembers().get(0).getId());
  }

}
