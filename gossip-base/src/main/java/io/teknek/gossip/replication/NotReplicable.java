package io.teknek.gossip.replication;

import io.teknek.gossip.LocalMember;
import io.teknek.gossip.model.Base;

/**
 * Replicable implementation which never replicates data on any node
 *
 * @param <T> A subtype of the class {@link io.teknek.gossip.model.Base} which uses this interface
 * @see Replicable
 */
public class NotReplicable<T extends Base> implements Replicable<T> {
  
  @Override
  public boolean shouldReplicate(LocalMember me, LocalMember destination, T message) {
    return false;
  }
}
