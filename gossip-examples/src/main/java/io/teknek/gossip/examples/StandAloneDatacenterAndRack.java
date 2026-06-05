
package io.teknek.gossip.examples;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.teknek.gossip.GossipSettings;
import io.teknek.gossip.RemoteMember;
import io.teknek.gossip.manager.DatacenterRackAwareActiveGossiper;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;

public class StandAloneDatacenterAndRack extends StandAloneExampleBase {

  public static void main(String[] args) throws InterruptedException, IOException {
    StandAloneDatacenterAndRack example = new StandAloneDatacenterAndRack(args);
    boolean willRead = true;
    example.exec(willRead);
  }

  StandAloneDatacenterAndRack(String[] args) {
    args = super.checkArgsForClearFlag(args);
    initGossipManager(args);
  }

  void initGossipManager(String[] args) {
    GossipSettings s = new GossipSettings();
    s.setWindowSize(1000);
    s.setGossipInterval(100);
    s.setActiveGossipClass(DatacenterRackAwareActiveGossiper.class.getName());
    Map<String, String> gossipProps = new HashMap<>();
    gossipProps.put("sameRackGossipIntervalMs", "2000");
    gossipProps.put("differentDatacenterGossipIntervalMs", "10000");
    s.setActiveGossipProperties(gossipProps);
    Map<String, String> props = new HashMap<>();
    props.put(DatacenterRackAwareActiveGossiper.DATACENTER, args[4]);
    props.put(DatacenterRackAwareActiveGossiper.RACK, args[5]);
    GossipManager manager = GossipManagerBuilder.newBuilder().cluster("mycluster")
            .uri(URI.create(args[0])).id(args[1]).gossipSettings(s)
            .gossipMembers(
                    Arrays.asList(new RemoteMember("mycluster", URI.create(args[2]), args[3])))
            .properties(props).build();
    manager.init();
    setGossipService(manager);
  }

  @Override
  void printValues(GossipManager gossipService) {
    return;
  }

}
