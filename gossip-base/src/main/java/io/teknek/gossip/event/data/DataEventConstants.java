package io.teknek.gossip.event.data;

public class DataEventConstants {
  
  // MetricRegistry
  public static final String PER_NODE_DATA_SUBSCRIBERS_SIZE
          = "gossip.event.data.pernode.subscribers.size";
  public static final String PER_NODE_DATA_SUBSCRIBERS_QUEUE_SIZE
          = "gossip.event.data.pernode.subscribers.queue.size";
  public static final String SHARED_DATA_SUBSCRIBERS_SIZE
          = "gossip.event.data.shared.subscribers.size";
  public static final String SHARED_DATA_SUBSCRIBERS_QUEUE_SIZE
          = "gossip.event.data.shared.subscribers.queue.size";
  
  // Thread pool
  public static final int PER_NODE_DATA_QUEUE_SIZE = 64;
  public static final int PER_NODE_DATA_CORE_POOL_SIZE = 1;
  public static final int PER_NODE_DATA_MAX_POOL_SIZE = 30;
  public static final int PER_NODE_DATA_KEEP_ALIVE_TIME_SECONDS = 1;
  public static final int SHARED_DATA_QUEUE_SIZE = 64;
  public static final int SHARED_DATA_CORE_POOL_SIZE = 1;
  public static final int SHARED_DATA_MAX_POOL_SIZE = 30;
  public static final int SHARED_DATA_KEEP_ALIVE_TIME_SECONDS = 1;
  
}
