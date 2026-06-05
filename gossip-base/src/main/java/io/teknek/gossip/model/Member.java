package io.teknek.gossip.model;

import java.util.Map;

public class Member {

  private String cluster;
  private String uri;
  private String id;
  private Long heartbeat;
  private Map<String,String> properties;
  
  public Member(){
    
  }
  
  public Member(String cluster, String uri, String id, Long heartbeat){
    this.cluster = cluster;
    this.uri = uri;
    this.id = id;
    this.heartbeat = heartbeat;
  }

  public String getCluster() {
    return cluster;
  }

  public void setCluster(String cluster) {
    this.cluster = cluster;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getHeartbeat() {
    return heartbeat;
  }

  public void setHeartbeat(Long heartbeat) {
    this.heartbeat = heartbeat;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  @Override
  public String toString() {
    return "Member [cluster=" + cluster + ", uri=" + uri + ", id=" + id + ", heartbeat="
            + heartbeat + ", properties=" + properties + "]";
  }
  
}
