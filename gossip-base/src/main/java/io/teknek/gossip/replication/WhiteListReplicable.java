package io.teknek.gossip.replication;

import io.teknek.gossip.LocalMember;
import io.teknek.gossip.model.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Replicable implementation which replicates data to given set of nodes.
 *
 * @param <T> A subtype of the class {@link io.teknek.gossip.model.Base} which uses this interface
 * @see Replicable
 */
public class WhiteListReplicable<T extends Base> implements Replicable<T> {
  
  private final List<LocalMember> whiteListMembers;
  
  public WhiteListReplicable(List<LocalMember> whiteListMembers) {
    if (whiteListMembers == null) {
      this.whiteListMembers = new ArrayList<>();
    } else {
      this.whiteListMembers = whiteListMembers;
    }
  }

  public List<LocalMember> getWhiteListMembers() {
    return whiteListMembers;
  }

  @Override
  public boolean shouldReplicate(LocalMember me, LocalMember destination, T message) {
    return whiteListMembers.contains(destination);
  }
}
