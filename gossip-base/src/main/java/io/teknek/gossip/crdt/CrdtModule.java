package io.teknek.gossip.crdt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.teknek.gossip.LocalMember;
import io.teknek.gossip.lock.vote.MajorityVote;
import io.teknek.gossip.lock.vote.Vote;
import io.teknek.gossip.lock.vote.VoteCandidate;
import io.teknek.gossip.replication.BlackListReplicable;
import io.teknek.gossip.replication.Replicable;
import io.teknek.gossip.replication.WhiteListReplicable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

abstract class OrSetMixin<E> {
  @JsonCreator
  OrSetMixin(@JsonProperty("elements") Map<E, Set<UUID>> w, @JsonProperty("tombstones") Map<E, Set<UUID>> h) { }
  @JsonProperty("elements") abstract Map<E, Set<UUID>> getElements();
  @JsonProperty("tombstones") abstract Map<E, Set<UUID>> getTombstones();
  @JsonIgnore abstract boolean isEmpty();
}

abstract class LWWSetMixin<ElementType> {
  @JsonCreator
  LWWSetMixin(@JsonProperty("data") Map<ElementType, LwwSet.Timestamps> struct) { }
  @JsonProperty("data") abstract Map<ElementType, LwwSet.Timestamps> getStruct();
}

abstract class LWWSetTimestampsMixin {
  @JsonCreator
  LWWSetTimestampsMixin(@JsonProperty("add") long latestAdd, @JsonProperty("remove") long latestRemove) { }
  @JsonProperty("add") abstract long getLatestAdd();
  @JsonProperty("remove") abstract long getLatestRemove();
}

abstract class MaxChangeSetMixin<E> {
  @JsonCreator
  MaxChangeSetMixin(@JsonProperty("data") Map<E, Integer> struct) { }
  @JsonProperty("data") abstract Map<E, Integer> getStruct();
}

abstract class TwoPhaseSetMixin<E> {
  @JsonCreator
  TwoPhaseSetMixin(@JsonProperty("added") Set<E> added, @JsonProperty("removed") Set<E> removed) { }
  @JsonProperty("added") abstract Set<E> getAdded();
  @JsonProperty("removed") abstract Set<E> getRemoved();
}

abstract class GrowOnlySetMixin<E>{
  @JsonCreator
  GrowOnlySetMixin(@JsonProperty("elements") Set<E> elements){ }
  @JsonProperty("elements") abstract Set<E> getElements();
  @JsonIgnore abstract boolean isEmpty();
}

abstract class GrowOnlyCounterMixin {
  @JsonCreator
  GrowOnlyCounterMixin(@JsonProperty("counters") Map<String, Long> counters) { }
  @JsonProperty("counters") abstract Map<String, Long> getCounters();
}

abstract class PNCounterMixin {
  @JsonCreator
  PNCounterMixin(@JsonProperty("p-counters") Map<String, Long> up, @JsonProperty("n-counters") Map<String,Long> down) { }
  @JsonProperty("p-counters") abstract Map<String, Long> getPCounters();
  @JsonProperty("n-counters") abstract Map<String, Long> getNCounters();
}

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
abstract class ReplicableMixin {

}

abstract class WhiteListReplicableMixin {
  @JsonCreator
  WhiteListReplicableMixin(@JsonProperty("whiteListMembers") List<LocalMember> whiteListMembers) { }
  @JsonProperty("whiteListMembers") abstract List<LocalMember> getWhiteListMembers();
}

abstract class BlackListReplicableMixin {
  @JsonCreator
  BlackListReplicableMixin(@JsonProperty("blackListMembers") List<LocalMember> blackListMembers) { }
  @JsonProperty("blackListMembers") abstract List<LocalMember> getBlackListMembers();
}

abstract class VoteCandidateMixin {
  @JsonCreator
  VoteCandidateMixin(
          @JsonProperty("candidateNodeId") String candidateNodeId,
          @JsonProperty("votingKey") String votingKey,
          @JsonProperty("votes") Map<String, Vote> votes
  ) { }
}

abstract class VoteMixin {
  @JsonCreator
  VoteMixin(
          @JsonProperty("votingNode") String votingNode,
          @JsonProperty("voteValue") Boolean voteValue,
          @JsonProperty("voteExchange") Boolean voteExchange,
          @JsonProperty("liveMembers") List<String> liveMembers,
          @JsonProperty("deadMembers") List<String> deadMembers
  ) { }
}

abstract class MajorityVoteMixin<E>{
  @JsonCreator
  MajorityVoteMixin(@JsonProperty("voteCandidates") Map<String, VoteCandidate> voteCandidateMap){ }
}

//If anyone wants to take a stab at this. please have at it
//https://github.com/FasterXML/jackson-datatype-guava/blob/master/src/main/java/com/fasterxml/jackson/datatype/guava/ser/MultimapSerializer.java
public class CrdtModule extends SimpleModule {

  private static final long serialVersionUID = 6134836523275023418L;

  public CrdtModule() {
    super("CrdtModule", new Version(0, 0, 0, "0.0.0", "io.teknek.gossip", "gossip"));
  }

  @Override
  public void setupModule(SetupContext context) {
    context.setMixInAnnotations(OrSet.class, OrSetMixin.class);
    context.setMixInAnnotations(GrowOnlySet.class, GrowOnlySetMixin.class);
    context.setMixInAnnotations(GrowOnlyCounter.class, GrowOnlyCounterMixin.class);
    context.setMixInAnnotations(PNCounter.class, PNCounterMixin.class);
    context.setMixInAnnotations(LwwSet.class, LWWSetMixin.class);
    context.setMixInAnnotations(LwwSet.Timestamps.class, LWWSetTimestampsMixin.class);
    context.setMixInAnnotations(MaxChangeSet.class, MaxChangeSetMixin.class);
    context.setMixInAnnotations(TwoPhaseSet.class, TwoPhaseSetMixin.class);
    context.setMixInAnnotations(Replicable.class, ReplicableMixin.class);
    context.setMixInAnnotations(WhiteListReplicable.class, WhiteListReplicableMixin.class);
    context.setMixInAnnotations(BlackListReplicable.class, BlackListReplicableMixin.class);
    context.setMixInAnnotations(MajorityVote.class, MajorityVoteMixin.class);
    context.setMixInAnnotations(VoteCandidate.class, VoteCandidateMixin.class);
    context.setMixInAnnotations(Vote.class, VoteMixin.class);
  }

}

