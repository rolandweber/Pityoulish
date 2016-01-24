/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import java.util.List;


/**
 * A batch of {@link Message messages} with a continuation marker.
 * The latter is specific to the {@link MessageBoard board}
 * from which this batch was obtained.
 */
public interface MessageBatch
{
  /**
   * Obtains the messages in this batch.
   *
   * @return  the read-only list of messages in this batch
   */
  public List<Message> getMessages()
    ;


  /**
   * Obtains the continuation marker.
   * The marker can be used to fetch subsequent messages from the
   * {@link MessageBoard}.
   *
   * @return    an opaque, board-specific marker that indicates where
   *            on the board this list ends
   */
  public String getMarker()
    ;


  /**
   * Indicates whether messages might have been missed.
   * When a {@link #getMarker marker} is used to fetch subsequent messages
   * from a {@link MessageBoard}, this flag indicates that messages might
   * have dropped off the board between the preceding list and this list.
   *
   * @return    <code>true</code> if messages might have been missed;
   *            <code>false</code> if this list is consecutive to the
   *            preceding one, or if this is a fresh list
   */
  public boolean isDiscontinuous()
    ;

}
