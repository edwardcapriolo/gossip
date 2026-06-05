package io.teknek.gossip.model;

import io.teknek.gossip.replication.AllReplicable;
import io.teknek.gossip.replication.Replicable;

public class SharedDataMessage extends Base {

  private String nodeId;
  private String key;
  private Object payload;
  private Long timestamp;
  private Long expireAt;
  private Replicable<SharedDataMessage> replicable;

  public String getNodeId() {
    return nodeId;
  }
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public Object getPayload() {
    return payload;
  }
  public void setPayload(Object payload) {
    this.payload = payload;
  }
  public Long getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }
  public Long getExpireAt() {
    return expireAt;
  }
  public void setExpireAt(Long expireAt) {
    this.expireAt = expireAt;
  }
  
  public Replicable<SharedDataMessage> getReplicable() {
    return replicable;
  }
  
  public void setReplicable(Replicable<SharedDataMessage> replicable) {
    this.replicable = replicable;
  }
  
  @Override
  public String toString() {
    return "SharedGossipDataMessage [nodeId=" + nodeId + ", key=" + key + ", payload=" + payload
            + ", timestamp=" + timestamp + ", expireAt=" + expireAt
            + ", replicable=" + replicable + "]";
  }
}

