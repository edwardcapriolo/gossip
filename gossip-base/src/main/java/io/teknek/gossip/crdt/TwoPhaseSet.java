package io.teknek.gossip.crdt;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
  Two-Phase CrdtSet.
  You can add element only once and remove only once.
  You cannot remove element which is not present.

  Read more: https://github.com/aphyr/meangirls#2p-set
  You can view examples of usage in tests:
  TwoPhaseSetTest - unit tests
  DataTest - integration test with 2 nodes, TwoPhaseSet was serialized/deserialized, sent between nodes, merged
*/

public class TwoPhaseSet<ElementType> implements CrdtAddRemoveSet<ElementType, Set<ElementType>, TwoPhaseSet<ElementType>> {
  private final Set<ElementType> added;
  private final Set<ElementType> removed;

  public TwoPhaseSet(){
    added = new HashSet<>();
    removed = new HashSet<>();
  }

  @SafeVarargs
  public TwoPhaseSet(ElementType... elements){
    this(new HashSet<>(Arrays.asList(elements)));
  }

  public TwoPhaseSet(Set<ElementType> set){
    this();
    for (ElementType e : set){
      added.add(e);
    }
  }

  public TwoPhaseSet(TwoPhaseSet<ElementType> first, TwoPhaseSet<ElementType> second){
    BiFunction<Set<ElementType>, Set<ElementType>, Set<ElementType>> mergeSets = (f, s) ->
        Stream.concat(f.stream(), s.stream()).collect(Collectors.toSet());

    added = mergeSets.apply(first.added, second.added);
    removed = mergeSets.apply(first.removed, second.removed);
  }

  TwoPhaseSet(Set<ElementType> added, Set<ElementType> removed){
    this.added = added;
    this.removed = removed;
  }

  Set<ElementType> getAdded(){
    return added;
  }

  Set<ElementType> getRemoved(){
    return removed;
  }

  public TwoPhaseSet<ElementType> add(ElementType e){
    if (removed.contains(e) || added.contains(e)){
      return this;
    }
    return this.merge(new TwoPhaseSet<>(e));
  }

  public TwoPhaseSet<ElementType> remove(ElementType e){
    if (removed.contains(e) || !added.contains(e)){
      return this;
    }
    Set<ElementType> eSet = new HashSet<>(Collections.singletonList(e));
    return this.merge(new TwoPhaseSet<>(eSet, eSet));
  }

  @Override
  public TwoPhaseSet<ElementType> merge(TwoPhaseSet<ElementType> other){
    return new TwoPhaseSet<>(this, other);
  }

  @Override
  public Set<ElementType> value(){
    return added.stream().filter(e -> !removed.contains(e)).collect(Collectors.toSet());
  }

  @Override
  public TwoPhaseSet<ElementType> optimize(){
    return new TwoPhaseSet<>(value(), removed);
  }

  @Override
  public boolean equals(Object obj){
    return this == obj || (obj != null && getClass() == obj.getClass() && value().equals(((TwoPhaseSet) obj).value()));
  }
}
