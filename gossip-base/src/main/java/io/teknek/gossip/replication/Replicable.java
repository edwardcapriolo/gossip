package io.teknek.gossip.replication;

import io.teknek.gossip.LocalMember;
import io.teknek.gossip.model.Base;

/**
 * This interface is used to determine whether a data item needs to be replicated to
 * another gossip member.
 *
 * @param <T> A subtype of the class {@link io.teknek.gossip.model.Base} which uses this interface
 */
public interface Replicable<T extends Base> {
  /**
   * Test for a given data item needs to be replicated.
   * @param me node that the data item is going to transmit from.
   * @param destination target node to replicate.
   * @param message this parameter is currently ignored
   * @return true if the data item needs to be replicated to the destination. Otherwise false.
   */
  boolean shouldReplicate(LocalMember me, LocalMember destination, T message);
}
