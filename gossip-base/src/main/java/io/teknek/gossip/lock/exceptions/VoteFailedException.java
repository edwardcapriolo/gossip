package io.teknek.gossip.lock.exceptions;

/**
 * This exception is thrown when the lock based voting is failed.
 */
public class VoteFailedException extends Exception {
  /**
   * Constructs a new VoteFailedException with the specified detail message.
   *
   * @param message the detail message.
   */
  public VoteFailedException(String message) {
    super(message);
  }

  /**
   * Constructs a new VoteFailedException with the specified detail message and
   * cause.
   *
   * @param message the detail message
   * @param cause   the cause for this exception
   */
  public VoteFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
