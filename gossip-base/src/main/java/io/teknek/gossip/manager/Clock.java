package io.teknek.gossip.manager;

public interface Clock {

  long currentTimeMillis();
  long nanoTime();
  
}
