package io.teknek.gossip;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;
import io.teknek.gossip.manager.PassiveGossipConstants;
import io.teknek.gossip.secure.KeyTool;
import org.junit.Assert;
import org.junit.Test;

import io.teknek.tunit.TUnit;

public class SignedMessageTest extends AbstractIntegrationBase {

  private GossipSettings gossiperThatSigns(){
    GossipSettings settings = new GossipSettings();
    settings.setPersistRingState(false);
    settings.setPersistDataState(false);
    settings.setSignMessages(true);
    return settings;
  }
  
  private GossipSettings gossiperThatSigns(String keysDir){
    GossipSettings settings = gossiperThatSigns();
    settings.setPathToKeyStore(Objects.requireNonNull(keysDir));
    return settings;
  }
  
  @Test
  public void dataTest() throws InterruptedException, URISyntaxException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
    final String keys = System.getProperty("java.io.tmpdir") + "/keys";
    GossipSettings settings = gossiperThatSigns(keys);
    setup(keys);
    String cluster = UUID.randomUUID().toString();
    List<Member> startupMembers = new ArrayList<>();
    for (int i = 1; i < 2; ++i) {
      URI uri = new URI("udp://" + "127.0.0.1" + ":" + (30000 + i));
      startupMembers.add(new RemoteMember(cluster, uri, i + ""));
    }
    final List<GossipManager> clients = new ArrayList<>();
    for (int i = 1; i < 3; ++i) {
      URI uri = new URI("udp://" + "127.0.0.1" + ":" + (30000 + i));
      GossipManager gossipService = GossipManagerBuilder.newBuilder()
              .cluster(cluster)
              .uri(uri)
              .id(i + "")
              .gossipMembers(startupMembers)
              .gossipSettings(settings)
              .build();
      gossipService.init();
      clients.add(gossipService);
    }
    assertTwoAlive(clients);
    assertOnlySignedMessages(clients);
    cleanup(keys, clients);
  }
  
  private void assertTwoAlive(List<GossipManager> clients){
    TUnit.assertThat(() -> {
      int total = 0;
      for (int i = 0; i < clients.size(); ++i) {
        total += clients.get(i).getLiveMembers().size();
      }
      return total;
    }).afterWaitingAtMost(20, TimeUnit.SECONDS).isEqualTo(2);
  }
  
  private void assertOnlySignedMessages(List<GossipManager> clients){
    Assert.assertEquals(0, clients.get(0).getRegistry()
            .meter(PassiveGossipConstants.UNSIGNED_MESSAGE).getCount());
    Assert.assertTrue(clients.get(0).getRegistry()
            .meter(PassiveGossipConstants.SIGNED_MESSAGE).getCount() > 0);
  }
  
  private void cleanup(String keys, List<GossipManager> clients){
    new File(keys, "1").delete();
    new File(keys, "2").delete();
    new File(keys).delete();
    for (int i = 0; i < clients.size(); ++i) {
      clients.get(i).shutdown();
    }
  }
  
  private void setup(String keys) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
    new File(keys).mkdir();
    KeyTool.generatePubandPrivateKeyFiles(keys, "1");
    KeyTool.generatePubandPrivateKeyFiles(keys, "2");
  }
}
