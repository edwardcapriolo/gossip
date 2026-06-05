package io.teknek.gossip;

import io.teknek.tunit.TUnit;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;
import io.teknek.gossip.model.PerNodeDataMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@RunWith(Parameterized.class)
public class PerNodeDataEventTest extends AbstractIntegrationBase {
  
  private String receivedKey = "";
  private String receivingNodeId = "";
  private Object receivingNodeDataNewValue = "";
  private Object receivingNodeDataOldValue = "";
  private Semaphore lock = new Semaphore(0);
  private int base;
  private boolean bulkTransfer;

  public PerNodeDataEventTest(int base, boolean bulkTransfer) {
    this.base = base;
    this.bulkTransfer = bulkTransfer;
  }

  @Parameterized.Parameters(name = "{index} bulkTransfer={1}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {50000, false}, {55000, true}
    });
  }

  @Test
  public void perNodeDataEventTest()
          throws InterruptedException, UnknownHostException, URISyntaxException {
    GossipSettings settings = new GossipSettings();
    settings.setPersistRingState(false);
    settings.setPersistDataState(false);
    settings.setBulkTransfer(bulkTransfer);
    String cluster = UUID.randomUUID().toString();
    int seedNodes = 1;
    List<Member> startupMembers = new ArrayList<>();
    for (int i = 1; i < seedNodes + 1; ++i) {
      URI uri = new URI("udp://" + "127.0.0.1" + ":" + (base + i));
      startupMembers.add(new RemoteMember(cluster, uri, i + ""));
    }
    final List<GossipManager> clients = new ArrayList<>();
    final int clusterMembers = 2;
    for (int i = 1; i < clusterMembers + 1; ++i) {
      URI uri = new URI("udp://" + "127.0.0.1" + ":" + (base + i));
      GossipManager gossipService = GossipManagerBuilder.newBuilder().cluster(cluster).uri(uri)
              .id(i + "").gossipMembers(startupMembers).gossipSettings(settings).build();
      clients.add(gossipService);
      gossipService.init();
      register(gossipService);
    }
    
    // check whether the members are discovered
    TUnit.assertThat(() -> {
      int total = 0;
      for (int i = 0; i < clusterMembers; ++i) {
        total += clients.get(i).getLiveMembers().size();
      }
      return total;
    }).afterWaitingAtMost(20, TimeUnit.SECONDS).isEqualTo(2);
    
    // Adding new data to Node 1
    clients.get(0).gossipPerNodeData(getPerNodeData("category", "distributed"));
    
    // Node 2 is interested in data changes for the key "organization" and "category"
    clients.get(1).registerPerNodeDataSubscriber((nodeId, key, oldValue, newValue) -> {
      if (!key.equals("organization") && !key.equals("category")) return;
      receivingNodeId = nodeId;
      receivedKey = key;
      receivingNodeDataOldValue = oldValue;
      receivingNodeDataNewValue = newValue;
      lock.release();
    });
  
    // Node 2 first time adds Node 1 data
    lock.tryAcquire(10, TimeUnit.SECONDS);
    Assert.assertEquals("1", receivingNodeId);
    Assert.assertEquals("category", receivedKey);
    Assert.assertEquals(null, receivingNodeDataOldValue);
    Assert.assertEquals("distributed", receivingNodeDataNewValue);
  
    // Node 1 adds new per node data
    clients.get(0).gossipPerNodeData(getPerNodeData("organization", "apache"));
    // Node 2 adds new data key from Node 1
    lock.tryAcquire(10, TimeUnit.SECONDS);
    Assert.assertEquals("1", receivingNodeId);
    Assert.assertEquals("organization", receivedKey);
    Assert.assertEquals(null, receivingNodeDataOldValue);
    Assert.assertEquals("apache", receivingNodeDataNewValue);
  
    // Node 1 updates its value
    clients.get(0).gossipPerNodeData(getPerNodeData("organization", "apache-gossip"));
    // Node 2 updates existing value
    lock.tryAcquire(10, TimeUnit.SECONDS);
    Assert.assertEquals("1", receivingNodeId);
    Assert.assertEquals("organization", receivedKey);
    Assert.assertEquals("apache", receivingNodeDataOldValue);
    Assert.assertEquals("apache-gossip", receivingNodeDataNewValue);
    
  }
  
  private PerNodeDataMessage getPerNodeData(String key, String value) {
    PerNodeDataMessage g = new PerNodeDataMessage();
    g.setExpireAt(Long.MAX_VALUE);
    g.setKey(key);
    g.setPayload(value);
    g.setTimestamp(System.currentTimeMillis());
    return g;
  }
  
}
