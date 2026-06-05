package io.teknek.gossip.manager;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import io.teknek.gossip.GossipSettings;
import io.teknek.gossip.RemoteMember;
import org.junit.Assert;
import org.junit.Test;

public class RingPersistenceTest {

  @Test
  public void givenThatRingIsPersisted() throws UnknownHostException, InterruptedException, URISyntaxException {
    GossipSettings settings = new GossipSettings();
    File f = aGossiperPersists(settings);
    Assert.assertTrue(f.exists());
    aNewInstanceGetsRingInfo(settings);
    f.delete();
  }
  
  private File aGossiperPersists(GossipSettings settings) throws UnknownHostException, InterruptedException, URISyntaxException {
    GossipManager gossipService = GossipManagerBuilder.newBuilder()
            .cluster("a")
            .uri(new URI("udp://" + "127.0.0.1" + ":" + (29000 + 1)))
            .id("1")
            .gossipSettings(settings)
            .gossipMembers(
                    Arrays.asList(
                            new RemoteMember("a", new URI("udp://" + "127.0.0.1" + ":" + (29000 + 0)), "0"),
                            new RemoteMember("a", new URI("udp://" + "127.0.0.1" + ":" + (29000 + 2)), "2"))).build();
    gossipService.getRingState().writeToDisk();
    return GossipManager.buildRingStatePath(gossipService);
  }
  
  private void aNewInstanceGetsRingInfo(GossipSettings settings) throws UnknownHostException, InterruptedException, URISyntaxException {
    GossipManager gossipService2 = GossipManagerBuilder.newBuilder()
            .cluster("a")
            .uri(new URI("udp://" + "127.0.0.1" + ":" + (29000 + 1)))
            .id("1")
            .gossipSettings(settings).build();
    Assert.assertEquals(2, gossipService2.getMembers().size());
  }
  
}
