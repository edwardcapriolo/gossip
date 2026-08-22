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

  /**
   * @param shutdownAtNanos shutdown time in milliseconds. The method name is retained for wire compatibility.
   */
  public void setShutdownAtNanos(long shutdownAtNanos) {
    this.shutdownAtNanos = shutdownAtNanos;
  }

  @Override
  public String toString() {
    return "ShutdownMessage [shutdownAtMillis=" + shutdownAtNanos + ", nodeId=" + nodeId + "]";
  }
  
}
