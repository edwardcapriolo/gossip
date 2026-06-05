package io.teknek.gossip.lock.vote;

import java.util.List;

/**
 * Store a voter details.
 */
public class Vote {
  private final String votingNode;
  private final Boolean voteValue; // TODO: 7/16/17  weight?
  private Boolean voteExchange;
  private final List<String> liveMembers;
  private final List<String> deadMembers;

  public Vote(String votingNode, Boolean voteValue, Boolean voteExchange, List<String> liveMembers,
          List<String> deadMembers) {
    this.votingNode = votingNode;
    this.voteValue = voteValue;
    this.voteExchange = voteExchange;
    this.liveMembers = liveMembers;
    this.deadMembers = deadMembers;
  }

  public String getVotingNode() {
    return votingNode;
  }

  public Boolean getVoteValue() {
    return voteValue;
  }

  public Boolean getVoteExchange() {
    return voteExchange;
  }

  public void setVoteExchange(Boolean voteExchange) {
    this.voteExchange = voteExchange;
  }

  public List<String> getLiveMembers() {
    return liveMembers;
  }

  public List<String> getDeadMembers() {
    return deadMembers;
  }

  @Override
  public String toString() {
    return "votingNode=" + votingNode + ", voteValue=" + voteValue + ", liveMembers=" + liveMembers
            + ", deadMembers= " + deadMembers;
  }
}
