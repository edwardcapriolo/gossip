package io.teknek.gossip.crdt;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;

public class GrowOnlySetTest {

  @SuppressWarnings("rawtypes")
  @Test
  public void mergeTest(){
    ConcurrentHashMap<String, Crdt> a = new ConcurrentHashMap<>();
    GrowOnlySet<String> gset = new GrowOnlySet<>(Arrays.asList("a", "b"));
    Assert.assertEquals(gset, a.merge("a", gset, new CrdtBiFunctionMerge()));
    GrowOnlySet<String> over = new GrowOnlySet<>(Arrays.asList("b", "d"));
    Assert.assertEquals(new GrowOnlySet<>(Arrays.asList("a", "b", "d")), 
            a.merge("a", over, CrdtBiFunctionMerge::applyStatic));
  }
}
