package io.teknek.gossip.replication;

import io.teknek.gossip.LocalMember;
import io.teknek.gossip.model.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Replicable implementation which does not replicate data to given set of nodes.
 *
 * @param <T> A subtype of the class {@link io.teknek.gossip.model.Base} which uses this interface
 * @see Replicable
 */
public class BlackListReplicable<T extends Base> implements Replicable<T> {
  
  private final List<LocalMember> blackListMembers;
  
  public BlackListReplicable(List<LocalMember> blackListMembers) {
    if (blackListMembers == null) {
      this.blackListMembers = new ArrayList<>();
    } else {
      this.blackListMembers = blackListMembers;
    }
  }

  public List<LocalMember> getBlackListMembers() {
    return blackListMembers;
  }

  @Override
  public boolean shouldReplicate(LocalMember me, LocalMember destination, T message) {
    return !blackListMembers.contains(destination);
  }
}
