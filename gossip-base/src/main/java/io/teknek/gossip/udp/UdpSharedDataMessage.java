package io.teknek.gossip.udp;

import io.teknek.gossip.model.SharedDataMessage;

public class UdpSharedDataMessage extends SharedDataMessage implements Trackable {

  private String uriFrom;
  private String uuid;
  
  public String getUriFrom() {
    return uriFrom;
  }
  
  public void setUriFrom(String uriFrom) {
    this.uriFrom = uriFrom;
  }
  
  public String getUuid() {
    return uuid;
  }
  
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  public String toString() {
    return "UdpSharedGossipDataMessage [uriFrom=" + uriFrom + ", uuid=" + uuid + ", getNodeId()="
            + getNodeId() + ", getKey()=" + getKey() + ", getPayload()=" + getPayload()
            + ", getTimestamp()=" + getTimestamp() + ", getExpireAt()=" + getExpireAt()
            + ", getReplicable()=" + getReplicable() + "]";
  }

}
