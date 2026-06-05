package io.teknek.gossip.crdt;
/**
 * 
 * Immutable type 
 *
 * @param <SetType>
 * @param <MergeReturnType>
 */
public interface Crdt<SetType, MergeReturnType extends Crdt<SetType, MergeReturnType>> {

 
  MergeReturnType merge(MergeReturnType other);
  SetType value();
  /**
   * Called to self optimize. Some CRDTs may use some mechanism to clean up be 
   * removing obsolete data outside the scope of merging. IE this could clean up 
   * temporal values, old copies etc. 
   * @return the Crdt structure optimized 
   */
  MergeReturnType optimize();

}
