package io.teknek.gossip.transport;

import java.io.IOException;
import java.net.URI;

/** interface for manager that sends and receives messages that have already been serialized. */
public interface TransportManager {
  
  /** starts the active gossip thread responsible for reaching out to remote nodes. Not related to `startEndpoint()` */
  void startActiveGossiper();
  
  /** starts the passive gossip thread that receives messages from remote nodes. Not related to `startActiveGossiper()` */
  void startEndpoint();
  
  /** attempts to shutdown all threads. */
  void shutdown();
  
  /** sends a payload to an endpoint. */
  void send(URI endpoint, byte[] buf) throws IOException;
  
  /** gets the next payload being sent to this node */
  byte[] read() throws IOException;
}
