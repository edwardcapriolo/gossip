package io.teknek.gossip;

import java.net.URI;
import java.util.Map;

import io.teknek.gossip.accrual.FailureDetector;

/**
 * This object represent a gossip member with the properties known locally. These objects are stored
 * in the local list of gossip members.
 * 
 */
public class LocalMember extends Member {
  /** The failure detector for this member */
  private transient FailureDetector detector;

  /**
   * 
   * @param uri
   *          The uri of the member
   * @param id
   *          id of the node
   * @param heartbeat
   *          The current heartbeat
   */
  public LocalMember(String clusterName, URI uri, String id,
          long heartbeat, Map<String,String> properties, int windowSize, int minSamples, String distribution) {
    super(clusterName, uri, id, heartbeat, properties );
    detector = new FailureDetector(minSamples, windowSize, distribution);
  }

  protected LocalMember(){
    
  }
  
  public void recordHeartbeat(long now){
    detector.recordHeartbeat(now);
  }
  
  public Double detect(long now) {
    return detector.computePhiMeasure(now);
  }

  @Override
  public String toString() {
    Double d = null;
    try {
      d = detect(System.nanoTime());
    } catch (RuntimeException ex) {}
    return "LocalGossipMember [uri=" + uri + ", heartbeat=" + heartbeat + ", clusterName="
            + clusterName + ", id=" + id + ", currentdetect=" + d  +" ]";
  }

}
