package io.teknek.gossip.lock;

import io.teknek.gossip.lock.vote.RandomVoteSelector;
import io.teknek.gossip.lock.vote.VoteSelector;

/**
 * Stores the lock manager related settings.
 */
public class LockManagerSettings {
  // Time between vote updates in ms. Default is 1 second.
  private final int voteUpdateInterval;
  // Vote selection algorithm. Default is random voting
  private final VoteSelector voteSelector;
  // Number of nodes available for voting. Default is -1 (Auto calculate)
  private final int numberOfNodes;
  // Number of times to test for deadlock before preventing. Default is 3
  private final int deadlockDetectionThreshold;
  // Wait time between vote result calculation. Default is 1000
  private final int resultCalculationDelay;

  /**
   * Construct LockManagerSettings with default settings.
   */
  public static LockManagerSettings getLockManagerDefaultSettings() {
    return new LockManagerSettings(1000, new RandomVoteSelector(), -1, 3, 1000);
  }

  /**
   * Construct a custom LockManagerSettings
   *
   * @param voteUpdateInterval         Time between vote updates in milliseconds.
   * @param voteSelector               Vote selection algorithm. Cannot be null
   * @param numberOfNodes              Number of nodes available for voting. Set to negative value for auto calculate
   * @param deadlockDetectionThreshold Number of times to test for deadlock before preventing
   * @param resultCalculationDelay     Wait time between vote result calculation
   */
  public LockManagerSettings(int voteUpdateInterval, VoteSelector voteSelector, int numberOfNodes,
          int deadlockDetectionThreshold, int resultCalculationDelay) {
    this.voteUpdateInterval = voteUpdateInterval;
    this.voteSelector = voteSelector;
    this.numberOfNodes = numberOfNodes;
    this.deadlockDetectionThreshold = deadlockDetectionThreshold;
    this.resultCalculationDelay = resultCalculationDelay;

  }

  public int getVoteUpdateInterval() {
    return voteUpdateInterval;
  }

  public VoteSelector getVoteSelector() {
    return voteSelector;
  }

  public int getNumberOfNodes() {
    return numberOfNodes;
  }

  public int getDeadlockDetectionThreshold() {
    return deadlockDetectionThreshold;
  }

  public int getResultCalculationDelay() {
    return resultCalculationDelay;
  }
}
