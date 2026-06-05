
package io.teknek.gossip.protocol;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import io.teknek.gossip.GossipSettings;
import io.teknek.gossip.manager.PassiveGossipConstants;
import io.teknek.gossip.model.Base;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// doesn't serialize anything besides longs. Uses a static lookup table to read and write objects.
public class UnitTestProtocolManager implements ProtocolManager {
  
  // so it can be shared across gossipers. this works as long as each object has a different memory address.
  private static final Map<Long, Base> lookup = new ConcurrentHashMap<>();
  private final Meter meter;
  
  public UnitTestProtocolManager(GossipSettings settings, String id, MetricRegistry registry) {
    meter = settings.isSignMessages() ?
        registry.meter(PassiveGossipConstants.SIGNED_MESSAGE) :
        registry.meter(PassiveGossipConstants.UNSIGNED_MESSAGE);
  }
  
  private static byte[] longToBytes(long val) {
    byte[] b = new byte[8];
    b[7] = (byte) (val);
    b[6] = (byte) (val >>>  8);
    b[5] = (byte) (val >>> 16);
    b[4] = (byte) (val >>> 24);
    b[3] = (byte) (val >>> 32);
    b[2] = (byte) (val >>> 40);
    b[1] = (byte) (val >>> 48);
    b[0] = (byte) (val >>> 56);
    return b;
  }

  static long bytesToLong(byte[] b) {
    return ((b[7] & 0xFFL)) +
        ((b[6] & 0xFFL) << 8) +
        ((b[5] & 0xFFL) << 16) +
        ((b[4] & 0xFFL) << 24) +
        ((b[3] & 0xFFL) << 32) +
        ((b[2] & 0xFFL) << 40) +
        ((b[1] & 0xFFL) << 48) +
        (((long) b[0]) << 56);
  }
  
  @Override
  public byte[] write(Base message) throws IOException {
    long hashCode = System.identityHashCode(message);
    byte[] serialized = longToBytes(hashCode);
    lookup.put(hashCode, message);
    meter.mark();
    return serialized;
  }

  @Override
  public Base read(byte[] buf) throws IOException {
    long hashCode = bytesToLong(buf);
    return lookup.remove(hashCode);
  }
}
