package io.teknek.gossip.lock.vote;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * VoteSelector implementation which randomly select a voting node.
 */
public class RandomVoteSelector implements VoteSelector {

  @Override
  public String getVoteCandidateId(Set<String> voteCandidateIds) {
    List<String> voteCandidatesIds = new ArrayList<>(voteCandidateIds);
    return voteCandidatesIds.get(new Random().nextInt(voteCandidatesIds.size()));
  }
}
