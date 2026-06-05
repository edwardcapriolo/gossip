
package io.teknek.gossip;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractIntegrationBase {

  List <GossipManager> nodes = new ArrayList<>();
  
  public void register(GossipManager manager){
    nodes.add(manager);
  }

  public void generateStandardNodes(final int memberCount) throws URISyntaxException {
    if(nodes.size() > 0){
      after();
      nodes.clear();
    }
    GossipSettings settings = new GossipSettings();
    settings.setPersistRingState(false);
    settings.setPersistDataState(false);
    String cluster = UUID.randomUUID().toString();
    int seedNodes = 1;
    List<Member> startupMembers = new ArrayList<>();
    for (int i = 1; i < seedNodes + 1; ++i) {
      URI uri = new URI("udp://" + "127.0.0.1" + ":" + (50000 + i));
      startupMembers.add(new RemoteMember(cluster, uri, i + ""));
    }

    for (int i = 1; i < memberCount + 1; ++i) {
      URI uri = new URI("udp://" + "127.0.0.1" + ":" + (50000 + i));
      GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(cluster).uri(uri)
              .id(i + "").gossipMembers(startupMembers).gossipSettings(settings).build();
      gossipService.init();
      register(gossipService);
    }
  }
  @Before
  public void before(){
    nodes = new ArrayList<>();
  }
  
  @After
  public void after(){
    for (GossipManager node: nodes){
      if (node !=null){
        node.shutdown();
      }
    }
  }
  
}
