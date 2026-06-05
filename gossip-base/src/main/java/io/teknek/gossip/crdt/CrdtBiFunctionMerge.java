package io.teknek.gossip.crdt;

import java.util.function.BiFunction;

@SuppressWarnings("rawtypes")
public class CrdtBiFunctionMerge implements BiFunction<Crdt,Crdt,Crdt> {

  @SuppressWarnings("unchecked")
  @Override
  public Crdt apply(Crdt t, Crdt u) {
    if (t == null && u == null){
      return null;
    } else if (t == null){
      return u;
    } else if (u == null){
      return t;
    }
    if (! u.getClass().equals(t.getClass())){
      throw new IllegalArgumentException( "Can not merge " + t.getClass() + " "+ u.getClass());
    }
    return t.merge(u);
  }

  @SuppressWarnings("unchecked")
  public static Crdt applyStatic(Crdt t, Crdt u){
    if (t == null && u == null){
      return null;
    } else if (t == null){
      return u;
    } else if (u == null){
      return t;
    }
    if (! u.getClass().equals(t.getClass())){
      throw new IllegalArgumentException( "Can not merge " + t.getClass() + " "+ u.getClass());
    }
    return t.merge(u);
  }
}
