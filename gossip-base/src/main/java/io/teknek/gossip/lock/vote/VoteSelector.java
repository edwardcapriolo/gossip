package io.teknek.gossip.lock.vote;

import java.util.Set;

/**
 * This interface defines vote selection algorithm for the vote based locking.
 */
public interface VoteSelector {
  /**
   * This method get call by the lock manager of a node to decide which candidate need to be choose for voting.
   *
   * @param voteCandidateIds node id set for the vote candidates
   * @return selected node id to vote from the given vote candidate set.
   */
  String getVoteCandidateId(Set<String> voteCandidateIds);
}
