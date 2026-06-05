package io.teknek.gossip.crdt;

import io.teknek.gossip.manager.Clock;
import io.teknek.gossip.manager.SystemClock;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LwwSetTest extends AddRemoveStringSetTest<LwwSet<String>> {
  static private Clock clock = new SystemClock();

  LwwSet<String> construct(Set<String> set){
    return new LwwSet<>(set);
  }

  LwwSet<String> construct(){
    return new LwwSet<>();
  }

  @Test
  public void valueTest(){
    Map<Character, LwwSet.Timestamps> map = new HashMap<>();
    map.put('a', new LwwSet.Timestamps(1, 0));
    map.put('b', new LwwSet.Timestamps(1, 2));
    map.put('c', new LwwSet.Timestamps(3, 3));
    Set<Character> toTest = new HashSet<>();
    toTest.add('a'); // for 'a' addTime > removeTime
    toTest.add('c'); // for 'c' times are equal, we prefer add to remove
    Assert.assertEquals(new LwwSet<>(map).value(), toTest);
    Assert.assertEquals(new LwwSet<>(map), new LwwSet<>('a', 'c'));
  }

  @Test
  public void fakeTimeMergeTest(){
    // try to create LWWSet with time from future (simulate other process with its own clock) and validate result
    // check remove from the future
    Map<Integer, LwwSet.Timestamps> map = new HashMap<>();
    map.put(25, new LwwSet.Timestamps(clock.nanoTime(), Long.MAX_VALUE));
    LwwSet<Integer> lww = new LwwSet<>(map);
    Assert.assertEquals(lww, new LwwSet<Integer>());
    //create new LWWSet with element 25, and merge with other LWW which has remove in future
    Assert.assertEquals(new LwwSet<>(25).merge(lww), new LwwSet<Integer>());

    // add in future
    map.put(25, new LwwSet.Timestamps(Long.MAX_VALUE, 0));
    lww = new LwwSet<>(map);
    lww = lww.remove(25);
    Assert.assertEquals(lww, new LwwSet<>(25)); // 25 is still here
  }
}
