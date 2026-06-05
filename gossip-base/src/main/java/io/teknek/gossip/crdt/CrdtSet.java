package io.teknek.gossip.crdt;

import java.util.Set;

public interface CrdtSet<ElementType, SetType extends Set<ElementType>, R extends CrdtSet<ElementType, SetType, R>>
extends Crdt<SetType, R> {

}

