package io.teknek.gossip.lock.vote;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MajorityVoteTest {

  @Test
  public void differentCandidateMergeTest() {
    Map<String, VoteCandidate> voteCandidateMap1 = new HashMap<>();
    VoteCandidate candidateA = new VoteCandidate("1", "key1", generateVotes(1, 2, true, true));
    voteCandidateMap1.put("1", candidateA);
    MajorityVote first = new MajorityVote(voteCandidateMap1);

    Map<String, VoteCandidate> voteCandidateMap2 = new HashMap<>();
    VoteCandidate candidateB = new VoteCandidate("3", "key1", generateVotes(3, 4, true, false));
    voteCandidateMap2.put("3", candidateB);
    MajorityVote second = new MajorityVote(voteCandidateMap2);

    MajorityVote result = first.merge(second);

    Assert.assertTrue(result.value().get("1").getVotes().get("2").getVoteValue());
    Assert.assertTrue(!result.value().get("3").getVotes().get("4").getVoteValue());

  }

  @Test
  public void sameCandidateMergeTest() {
    Map<String, VoteCandidate> voteCandidateMap1 = new HashMap<>();
    VoteCandidate candidateA = new VoteCandidate("1", "key1", generateVotes(1, 2, true, true));
    voteCandidateMap1.put("1", candidateA);
    MajorityVote first = new MajorityVote(voteCandidateMap1);

    Map<String, VoteCandidate> voteCandidateMap2 = new HashMap<>();
    VoteCandidate candidateB = new VoteCandidate("1", "key1", generateVotes(3, 4, true, false));
    voteCandidateMap2.put("1", candidateB);
    MajorityVote second = new MajorityVote(voteCandidateMap2);

    MajorityVote result = first.merge(second);

    Assert.assertTrue(result.value().get("1").getVotes().get("2").getVoteValue());
    Assert.assertTrue(!result.value().get("1").getVotes().get("4").getVoteValue());

  }

  @Test
  public void sameVoteMergeTest() {
    Map<String, VoteCandidate> voteCandidateMap1 = new HashMap<>();
    VoteCandidate candidateA = new VoteCandidate("1", "key1", generateVotes(1, 2, true, true));
    voteCandidateMap1.put("1", candidateA);
    MajorityVote first = new MajorityVote(voteCandidateMap1);

    Map<String, VoteCandidate> voteCandidateMap2 = new HashMap<>();
    VoteCandidate candidateB = new VoteCandidate("1", "key1",
            generateVotes(2, 4, true, false, true));
    voteCandidateMap2.put("1", candidateB);
    MajorityVote second = new MajorityVote(voteCandidateMap2);

    MajorityVote result = first.merge(second);

    Assert.assertTrue(result.value().get("1").getVotes().get("2").getVoteValue());
  }

  public Map<String, Vote> generateVotes(int startingNodeId, int endNodeId, boolean... votes) {
    Map<String, Vote> voteMap = new HashMap<>();
    if ((endNodeId - startingNodeId + 1) > votes.length) {
      return voteMap;
    }
    for (int i = startingNodeId; i <= endNodeId; i++) {
      String nodeId = i + "";
      voteMap.put(nodeId, new Vote(nodeId, votes[i - startingNodeId], false, new ArrayList<>(),
              new ArrayList<>()));
    }
    return voteMap;
  }

}
