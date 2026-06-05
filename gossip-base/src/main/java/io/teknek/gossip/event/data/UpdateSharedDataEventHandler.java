package io.teknek.gossip.event.data;

/**
 * Event handler interface for shared data items.
 * Classes which implement this interface get notifications when shared data get changed.
 */
public interface UpdateSharedDataEventHandler {
  /**
   * This method get called when shared data get changed.
   *
   * @param key      key of the shared data item
   * @param oldValue previous value or null if the data is discovered for the first time
   * @param newValue updated value of the data item
   */
  void onUpdate(String key, Object oldValue, Object newValue);
  
}
