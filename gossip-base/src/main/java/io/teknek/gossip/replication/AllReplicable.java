package io.teknek.gossip.replication;

import io.teknek.gossip.LocalMember;
import io.teknek.gossip.model.Base;

/**
 * Replicable implementation which replicates data to any node. This is the default replication
 * strategy if a data item not specified its replication behaviour.
 *
 * @param <T> A subtype of the class {@link io.teknek.gossip.model.Base} which uses this interface
 * @see Replicable
 */
public class AllReplicable<T extends Base> implements Replicable<T> {
  
  @Override
  public boolean shouldReplicate(LocalMember me, LocalMember destination, T message) {
    return true;
  }
}
