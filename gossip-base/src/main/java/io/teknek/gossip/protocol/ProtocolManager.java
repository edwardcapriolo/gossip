package io.teknek.gossip.protocol;

import io.teknek.gossip.model.Base;

import java.io.IOException;

/** interface for managing message marshaling. */
public interface ProtocolManager {

  /** serialize a message
   * @param message
   * @return serialized message.
   * @throws IOException
   */
  byte[] write(Base message) throws IOException;

  /**
   * Reads the next message from a byte source.
   * @param buf
   * @return a gossip message.
   * @throws IOException
   */
  Base read(byte[] buf) throws IOException;
}
