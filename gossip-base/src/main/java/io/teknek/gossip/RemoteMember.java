package io.teknek.gossip;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * The object represents a gossip member with the properties as received from a remote gossip
 * member.
 * 
 */
public class RemoteMember extends Member {

  /**
   * Constructor.
   * 
   * @param uri
   *          A URI object containing IP/hostname and port
   * @param heartbeat
   *          The current heartbeat
   */
  public RemoteMember(String clusterName, URI uri, String id, long heartbeat, Map<String,String> properties) {
    super(clusterName, uri, id, heartbeat, properties);
  }

  public RemoteMember(String clusterName, URI uri, String id) {
    super(clusterName, uri, id, System.nanoTime(), new HashMap<String,String>());
  }

}
