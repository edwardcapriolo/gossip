package io.teknek.gossip.model;

public class ShutdownMessage extends Message {

  public static final String PER_NODE_KEY = "gossipcore.shutdowmessage";
  private long shutdownAtNanos;
  private String nodeId;
  
  public ShutdownMessage(){
    
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public long getShutdownAtNanos() {
    return shutdownAtNanos;
  }

  public void setShutdownAtNanos(long shutdownAtNanos) {
    this.shutdownAtNanos = shutdownAtNanos;
  }

  @Override
  public String toString() {
    return "ShutdownMessage [shutdownAtNanos=" + shutdownAtNanos + ", nodeId=" + nodeId + "]";
  }
  
}
