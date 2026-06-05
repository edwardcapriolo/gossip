package io.teknek.gossip;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class MemberTest {

  @Test
  public void testHashCodeFromGossip40() throws URISyntaxException {
    Assert.assertNotEquals(
            new LocalMember("mycluster", new URI("udp://4.4.4.4:1000"), "myid", 1, new HashMap<String,String>(), 10, 5, "exponential")
                    .hashCode(),
            new LocalMember("mycluster", new URI("udp://4.4.4.5:1005"), "yourid", 11, new HashMap<String,String>(), 11, 6, "exponential")
                    .hashCode());
  }
}
