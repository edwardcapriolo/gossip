package io.teknek.gossip.crdt;

import java.util.Map;

import io.teknek.gossip.manager.GossipManager;

public class PNCounter implements CrdtCounter<Long, PNCounter> {

  private final GrowOnlyCounter pCount;

  private final GrowOnlyCounter nCount;

  PNCounter(Map<String, Long> pCounters, Map<String, Long> nCounters) {
    pCount = new GrowOnlyCounter(pCounters);
    nCount = new GrowOnlyCounter(nCounters);
  }

  public PNCounter(PNCounter starter, Builder builder) {
    GrowOnlyCounter.Builder pBuilder = builder.makeGrowOnlyCounterBuilder(builder.pCount());
    pCount = new GrowOnlyCounter(starter.pCount, pBuilder);
    GrowOnlyCounter.Builder nBuilder = builder.makeGrowOnlyCounterBuilder(builder.nCount());
    nCount = new GrowOnlyCounter(starter.nCount, nBuilder);
  }

  public PNCounter(Builder builder) {
    GrowOnlyCounter.Builder pBuilder = builder.makeGrowOnlyCounterBuilder(builder.pCount());
    pCount = new GrowOnlyCounter(pBuilder);
    GrowOnlyCounter.Builder nBuilder = builder.makeGrowOnlyCounterBuilder(builder.nCount());
    nCount = new GrowOnlyCounter(nBuilder);
  }

  public PNCounter(GossipManager manager) {
    pCount = new GrowOnlyCounter(manager);
    nCount = new GrowOnlyCounter(manager);
  }

  public PNCounter(PNCounter starter, PNCounter other) {
    pCount = new GrowOnlyCounter(starter.pCount, other.pCount);
    nCount = new GrowOnlyCounter(starter.nCount, other.nCount);
  }

  @Override
  public PNCounter merge(PNCounter other) {
    return new PNCounter(this, other);
  }

  @Override
  public Long value() {
    long pValue = (long) pCount.value();
    long nValue = (long) nCount.value();
    return pValue - nValue;
  }

  @Override
  public PNCounter optimize() {
    return new PNCounter(pCount.getCounters(), nCount.getCounters());
  }

  @Override
  public boolean equals(Object obj) {
    if (getClass() != obj.getClass())
      return false;
    PNCounter other = (PNCounter) obj;
    return value().longValue() == other.value().longValue();
  }

  @Override
  public String toString() {
    return "PnCounter [pCount=" + pCount + ", nCount=" + nCount + ", value=" + value() + "]";
  }

  Map<String, Long> getPCounters() {
    return pCount.getCounters();
  }

  Map<String, Long> getNCounters() {
    return nCount.getCounters();
  }

  public static class Builder {

    private final GossipManager myManager;

    private long value = 0L;

    public Builder(GossipManager gossipManager) {
      myManager = gossipManager;
    }

    public long pCount() {
      if (value > 0) {
        return value;
      }
      return 0;
    }

    public long nCount() {
      if (value < 0) {
        return -value;
      }
      return 0;
    }

    public io.teknek.gossip.crdt.GrowOnlyCounter.Builder makeGrowOnlyCounterBuilder(long value) {
      io.teknek.gossip.crdt.GrowOnlyCounter.Builder ret = new io.teknek.gossip.crdt.GrowOnlyCounter.Builder(
              myManager);
      ret.increment(value);
      return ret;
    }

    public PNCounter.Builder increment(long delta) {
      value += delta;
      return this;
    }

    public PNCounter.Builder decrement(long delta) {
      value -= delta;
      return this;
    }
  }

}
