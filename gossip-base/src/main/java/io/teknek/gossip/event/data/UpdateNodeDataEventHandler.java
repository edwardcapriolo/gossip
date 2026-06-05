package io.teknek.gossip.event.data;

/**
 * Event handler interface for the per node data items.
 * Classes which implement this interface get notifications when per node data item get changed.
 */
public interface UpdateNodeDataEventHandler {
  
  /**
   * This method get called when a per node datum get changed.
   *
   * @param nodeId   id of the node that change the value
   * @param key      key of the datum
   * @param oldValue previous value of the datum or null if the datum is discovered
   *                 for the first time
   * @param newValue updated value of the datum
   */
  void onUpdate(String nodeId, String key, Object oldValue, Object newValue);
  
}
