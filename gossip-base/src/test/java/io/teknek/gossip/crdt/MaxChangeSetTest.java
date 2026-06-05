package io.teknek.gossip.crdt;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MaxChangeSetTest extends AddRemoveStringSetTest<MaxChangeSet<String>> {
  MaxChangeSet<String> construct(Set<String> set){
    return new MaxChangeSet<>(set);
  }

  MaxChangeSet<String> construct(){
    return new MaxChangeSet<>();
  }

  @Test
  public void valueTest(){
    Map<Character, Integer> struct = new HashMap<>();
    struct.put('a', 0);
    struct.put('b', 1);
    struct.put('c', 2);
    struct.put('d', 3);
    Set<Character> result = new HashSet<>();
    result.add('b');
    result.add('d');
    Assert.assertEquals(new MaxChangeSet<>(struct).value(), result);
  }

  @Test
  public void mergeTest(){
    MaxChangeSet<Integer> set1 = new MaxChangeSet<Integer>().add(1); // Set with one operation on 1
    MaxChangeSet<Integer> set2 = new MaxChangeSet<Integer>().add(1).remove(1); // two operations
    Assert.assertEquals(set1.merge(set2), new MaxChangeSet<Integer>()); // empty set wins

    set1 = set1.add(1).add(1).add(1);
    // empty set still wins, repetitive operations do nothing, don't increase number of operations
    Assert.assertEquals(set1.merge(set2), new MaxChangeSet<Integer>());

    set1 = set1.remove(1).add(1); // 3 operations
    Assert.assertEquals(set1.merge(set2), new MaxChangeSet<>(1)); // full set wins now

    set2 = set2.remove(1).remove(1).remove(1);
    // full set still wins, repetitive removes don't increase number of operations too
    Assert.assertEquals(set1.merge(set2), new MaxChangeSet<>(1));
  }
}
