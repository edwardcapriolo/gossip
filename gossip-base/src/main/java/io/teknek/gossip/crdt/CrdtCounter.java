package io.teknek.gossip.crdt;

public interface CrdtCounter<ValueType extends Number, R extends CrdtCounter<ValueType, R>>
        extends Crdt<ValueType, R> {
  
}

