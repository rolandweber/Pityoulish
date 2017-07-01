/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.follow;

/**
 * Tracks information about message batches.
 * The information is up to date only
 * after a full message batch has been processed.
 */
public interface MessageBatchTracker
{
  /**
   * Obtains the continuation marker for the batch.
   */
  public String getMarker()
    ;

  /**
   * Obtains the number of messages in the batch.
   */
  public int getSize()
    ;

}
