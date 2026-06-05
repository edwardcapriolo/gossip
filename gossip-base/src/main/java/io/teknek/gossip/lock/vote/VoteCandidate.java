package io.teknek.gossip.lock.vote;

import java.util.Map;
import java.util.Objects;

/**
 * Stores the vote candidate details and its votes.
 */
public class VoteCandidate {

  private final String candidateNodeId;
  private final String votingKey;
  private final Map<String, Vote> votes;

  public VoteCandidate(String candidateNodeId, String votingKey, Map<String, Vote> votes) {

    this.candidateNodeId = candidateNodeId;
    this.votingKey = votingKey;
    this.votes = votes;
  }

  public String getCandidateNodeId() {
    return candidateNodeId;
  }

  public String getVotingKey() {
    return votingKey;
  }

  public Map<String, Vote> getVotes() {
    return votes;
  }

  public void addVote(Vote vote) {
    votes.put(vote.getVotingNode(), vote);
  }

  @Override
  public int hashCode() {
    return Objects.hash(candidateNodeId, votingKey);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof VoteCandidate))
      return false;
    if (obj == this)
      return true;
    VoteCandidate other = (VoteCandidate) obj;
    return this.candidateNodeId.equals(other.candidateNodeId) && this.votingKey
            .equals(other.votingKey);
  }

  @Override
  public String toString() {
    return "candidateNodeId=" + candidateNodeId + ", votingKey=" + votingKey + ", votes= " + votes;
  }
}
