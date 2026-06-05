package io.teknek.gossip.replication;

import io.teknek.gossip.LocalMember;
import io.teknek.gossip.manager.DatacenterRackAwareActiveGossiper;
import io.teknek.gossip.model.Base;

/**
 * Replicable implementation which does replicate data only in the same data center.
 *
 * @param <T> A subtype of the class {@link io.teknek.gossip.model.Base} which uses this interface
 * @see Replicable
 */
public class DataCenterReplicable<T extends Base> implements Replicable<T> {
  
  @Override
  public boolean shouldReplicate(LocalMember me, LocalMember destination, T message) {
    if (!me.getProperties().containsKey(DatacenterRackAwareActiveGossiper.DATACENTER)) {
      // replicate to others if I am not belong to any data center
      return true;
    } else if (!destination.getProperties()
            .containsKey(DatacenterRackAwareActiveGossiper.DATACENTER)) {
      // Do not replicate if the destination data center is not defined
      return false;
    } else {
      return me.getProperties().get(DatacenterRackAwareActiveGossiper.DATACENTER)
              .equals(destination.getProperties().get(DatacenterRackAwareActiveGossiper.DATACENTER));
    }
  }
}
