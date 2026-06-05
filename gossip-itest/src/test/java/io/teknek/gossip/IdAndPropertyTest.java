package io.teknek.gossip;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit; 

import io.teknek.gossip.manager.DatacenterRackAwareActiveGossiper;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;
import org.junit.jupiter.api.Test;

import io.teknek.tunit.TUnit;

public class IdAndPropertyTest extends AbstractIntegrationBase {

  @Test
  public void testDatacenterRackGossiper() throws URISyntaxException, UnknownHostException, InterruptedException {
    GossipSettings settings = new GossipSettings();
    settings.setActiveGossipClass(DatacenterRackAwareActiveGossiper.class.getName());
    List<Member> startupMembers = new ArrayList<>();
    Map<String, String> x = new HashMap<>();
    x.put("a", "b");
    x.put("datacenter", "dc1");
    x.put("rack", "rack1");
    GossipManager gossipService1 = GossipManagerBuilder.newBuilder()
            .cluster("a")
            .uri(new URI("udp://" + "127.0.0.1" + ":" + (29000 + 0)))
            .id("0")
            .properties(x)
            .gossipMembers(startupMembers)
            .gossipSettings(settings).build();
    gossipService1.init();
    register(gossipService1);
    
    Map<String, String> y = new HashMap<>();
    y.put("a", "c");
    y.put("datacenter", "dc2");
    y.put("rack", "rack2");
    GossipManager gossipService2 = GossipManagerBuilder.newBuilder().cluster("a")
            .uri( new URI("udp://" + "127.0.0.1" + ":" + (29000 + 10)))
            .id("1")
            .properties(y)
            .gossipMembers(Arrays.asList(new RemoteMember("a",
                    new URI("udp://" + "127.0.0.1" + ":" + (29000 + 0)), "0")))
            .gossipSettings(settings).build();
    gossipService2.init();
    register(gossipService2);
    
    TUnit.assertThat(() -> { 
      String value = ""; 
      try {
        value = gossipService1.getLiveMembers().get(0).getProperties().get("a");
      } catch (RuntimeException e){ }
      return value;
    }).afterWaitingAtMost(10, TimeUnit.SECONDS).isEqualTo("c");
    
    TUnit.assertThat(() -> { 
      String value = ""; 
      try {
        value = gossipService2.getLiveMembers().get(0).getProperties().get("a");
      } catch (RuntimeException e){ }
      return value;
    }).afterWaitingAtMost(10, TimeUnit.SECONDS).isEqualTo("b");        
  }
}
