
package io.teknek.gossip.protocol.json;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.teknek.gossip.GossipSettings;
import io.teknek.gossip.Member;
import io.teknek.gossip.crdt.LwwSet;
import io.teknek.gossip.crdt.MaxChangeSet;
import io.teknek.gossip.crdt.OrSet;
import io.teknek.gossip.crdt.TwoPhaseSet;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;
import io.teknek.gossip.protocol.ProtocolManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JacksonTest {

  private static GossipSettings simpleSettings(GossipSettings settings) {
    settings.setPersistRingState(false);
    settings.setPersistDataState(false);
    settings.setTransportManagerClass("io.teknek.gossip.transport.UnitTestTransportManager");
    settings.setProtocolManagerClass("io.teknek.gossip.protocol.json.JacksonProtocolManager");
    return settings;
  }

  private static GossipSettings withSigning(GossipSettings settings) {
    settings.setSignMessages(true);
    return settings;
  }

  // formerly of SignedMessageTest.
  @Test(expected = IllegalArgumentException.class)
  public void ifSignMustHaveKeys()
          throws URISyntaxException, UnknownHostException, InterruptedException {
    String cluster = UUID.randomUUID().toString();
    GossipSettings settings = withSigning(simpleSettings(new GossipSettings()));
    List<Member> startupMembers = new ArrayList<>();
    URI uri = new URI("udp://" + "127.0.0.1" + ":" + (30000 + 1));
    GossipManager gossipService = GossipManagerBuilder.newBuilder()
            .cluster(cluster)
            .uri(uri)
            .id(1 + "")
            .gossipMembers(startupMembers)
            .gossipSettings(settings)
            .build();
    gossipService.init();
  }

  @Test
  public void jacksonSerialTest() throws InterruptedException, URISyntaxException, IOException {
    ObjectMapper objectMapper = JacksonProtocolManager.buildObjectMapper(simpleSettings(new GossipSettings()));

    OrSet<Integer> i = new OrSet<Integer>(new OrSet.Builder<Integer>().add(1).remove(1));
    String s = objectMapper.writeValueAsString(i);
    @SuppressWarnings("unchecked")
    OrSet<Integer> back = objectMapper.readValue(s, OrSet.class);
    Assert.assertEquals(back, i);
  }

  void jacksonCrdtSeDeTest(Object value, Class<?> cl){
    ObjectMapper objectMapper = JacksonProtocolManager.buildObjectMapper(simpleSettings(new GossipSettings()));

    try {
      String valueS = objectMapper.writeValueAsString(value);
      @SuppressWarnings("unchecked")
      Object parsedValue = objectMapper.readValue(valueS, cl);
      Assert.assertEquals(value, parsedValue);
    } catch (Exception e) {
      Assert.fail("Jackson se/de error");
    }
  }

  @Test
  public void jacksonOrSetTest(){
    jacksonCrdtSeDeTest(new OrSet<>("1", "2", "3").remove("2"), OrSet.class);
  }

  @Test
  public void jacksonLWWSetTest(){
    jacksonCrdtSeDeTest(new LwwSet<>("1", "2", "3").remove("2"), LwwSet.class);
  }

  @Test
  public void jacksonMaxChangeSetTest(){
    jacksonCrdtSeDeTest(new MaxChangeSet<>("1", "2", "3").remove("2"), MaxChangeSet.class);
  }

  @Test
  public void jacksonTwoPhaseSetTest(){
    jacksonCrdtSeDeTest(new TwoPhaseSet<>("1", "2", "3").remove("2"), TwoPhaseSet.class);
  }

  @Test
  public void testMessageEqualityAssumptions() {
    long timeA = System.nanoTime();
    long timeB = System.nanoTime();
    Assert.assertNotEquals(timeA, timeB);

    TestMessage messageA0 = new TestMessage(Long.toHexString(timeA));
    TestMessage messageA1 = new TestMessage(Long.toHexString(timeA));
    TestMessage messageB = new TestMessage(Long.toHexString(timeB));

    Assert.assertEquals(messageA0, messageA1);
    Assert.assertFalse(messageA0 == messageA1);
    Assert.assertNotEquals(messageA0, messageB);
    Assert.assertNotEquals(messageA1, messageB);
  }

  // ideally, we would test the serializability of every message type, but we just want to make sure this works in
  // basic cases.
  @Test
  public void testMessageSerializationRoundTrip() throws Exception {
    ProtocolManager mgr = new JacksonProtocolManager(simpleSettings(new GossipSettings()), "foo", new MetricRegistry());
    for (int i = 0; i < 100; i++) {
      TestMessage a = new TestMessage(Long.toHexString(System.nanoTime()));
      byte[] bytes = mgr.write(a);
      TestMessage b = (TestMessage) mgr.read(bytes);
      Assert.assertFalse(a == b);
      Assert.assertEquals(a, b);
      Assert.assertEquals(a.getMapOfThings(), b.getMapOfThings()); // concerned about that one, so explicit check.
    }
  }
}
