package io.teknek.gossip.accrual;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailureDetector {

  public static final Logger LOGGER = LoggerFactory.getLogger(FailureDetector.class);
  private final DescriptiveStatistics descriptiveStatistics;
  private final long minimumSamples;
  private volatile long latestHeartbeatMs = -1;
  private final String distribution;

  public FailureDetector(long minimumSamples, int windowSize, String distribution) {
    descriptiveStatistics = new DescriptiveStatistics(windowSize);
    this.minimumSamples = minimumSamples;
    this.distribution = distribution;
  }

  /**
   * Updates the statistics based on the delta between the last
   * heartbeat and supplied time
   *
   * @param now the time of the heartbeat in milliseconds
   */
  public synchronized void recordHeartbeat(long now) {
    if (now <= latestHeartbeatMs) {
      return;
    }
    if (latestHeartbeatMs != -1) {
      descriptiveStatistics.addValue(now - latestHeartbeatMs);
    }
    latestHeartbeatMs = now;
  }

  public synchronized Double computePhiMeasure(long now) {
    if (latestHeartbeatMs == -1 || descriptiveStatistics.getN() < minimumSamples) {
      return null;
    }
    long delta = now - latestHeartbeatMs;
    try {
      double probability;
      if (distribution.equals("normal")) {
        double standardDeviation = descriptiveStatistics.getStandardDeviation();
        standardDeviation = standardDeviation < 0.1 ? 0.1 : standardDeviation;
        probability = new NormalDistribution(descriptiveStatistics.getMean(), standardDeviation).cumulativeProbability(delta);
      } else {
        probability = new ExponentialDistribution(descriptiveStatistics.getMean()).cumulativeProbability(delta);
      }
      final double eps = 1e-12;
      if (1 - probability < eps) {
        probability = 1.0;
      }
      return -1.0d * Math.log10(1.0d - probability);
    } catch (IllegalArgumentException e) {
      LOGGER.debug("Unable to compute phi measure", e);
      return null;
    }
  }
}
