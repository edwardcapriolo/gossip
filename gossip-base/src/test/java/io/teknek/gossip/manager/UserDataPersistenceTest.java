package io.teknek.gossip.manager;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import io.teknek.gossip.GossipSettings;
import io.teknek.gossip.model.PerNodeDataMessage;
import io.teknek.gossip.model.SharedDataMessage;
import org.junit.Assert;
import org.junit.Test;

public class UserDataPersistenceTest {

  String nodeId = "1";
  
  private GossipManager sameService() throws URISyntaxException {  
    GossipSettings settings = new GossipSettings();
    settings.setTransportManagerClass("io.teknek.gossip.transport.UnitTestTransportManager");
    settings.setProtocolManagerClass("io.teknek.gossip.protocol.UnitTestProtocolManager");
    return GossipManagerBuilder.newBuilder()
            .cluster("a")
            .uri(new URI("udp://" + "127.0.0.1" + ":" + (29000 + 1)))
            .id(nodeId)
            .gossipSettings(settings).build();
  }
  
  @Test
  public void givenThatRingIsPersisted() throws UnknownHostException, InterruptedException, URISyntaxException {
    
    { //Create a gossip service and force it to persist its user data
      GossipManager gossipService = sameService();
      gossipService.init();
      gossipService.gossipPerNodeData(getToothpick());
      gossipService.gossipSharedData(getAnotherToothpick());
      gossipService.getUserDataState().writePerNodeToDisk();
      gossipService.getUserDataState().writeSharedToDisk();
      { //read the raw data and confirm
        ConcurrentHashMap<String, ConcurrentHashMap<String, PerNodeDataMessage>> l = gossipService.getUserDataState().readPerNodeFromDisk();
        Assert.assertEquals("red", ((AToothpick) l.get(nodeId).get("a").getPayload()).getColor());
      }
      {
        ConcurrentHashMap<String, SharedDataMessage> l = 
                gossipService.getUserDataState().readSharedDataFromDisk();
        Assert.assertEquals("blue", ((AToothpick) l.get("a").getPayload()).getColor());
      }
      gossipService.shutdown();
    }
    { //recreate the service and see that the data is read back in
      GossipManager gossipService = sameService();
      gossipService.init();
      Assert.assertEquals("red", ((AToothpick) gossipService.findPerNodeGossipData(nodeId, "a").getPayload()).getColor());
      Assert.assertEquals("blue", ((AToothpick) gossipService.findSharedGossipData("a").getPayload()).getColor());
      File f = GossipManager.buildSharedDataPath(gossipService);
      File g = GossipManager.buildPerNodeDataPath(gossipService);
      gossipService.shutdown();
      f.delete();
      g.delete();
    }
  }
  
  public PerNodeDataMessage getToothpick(){
    AToothpick a = new AToothpick();
    a.setColor("red");
    PerNodeDataMessage d = new PerNodeDataMessage();
    d.setExpireAt(Long.MAX_VALUE);
    d.setKey("a");
    d.setPayload(a);
    d.setTimestamp(System.currentTimeMillis());
    return d;
  }
  
  public SharedDataMessage getAnotherToothpick(){
    AToothpick a = new AToothpick();
    a.setColor("blue");
    SharedDataMessage d = new SharedDataMessage();
    d.setExpireAt(Long.MAX_VALUE);
    d.setKey("a");
    d.setPayload(a);
    d.setTimestamp(System.currentTimeMillis());
    return d;
  }
  
  public static class AToothpick {
    private String color;
    public AToothpick(){
      
    }
    public String getColor() {
      return color;
    }
    public void setColor(String color) {
      this.color = color;
    }
    
  }
}
