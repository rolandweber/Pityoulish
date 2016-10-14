/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.api;

import java.io.Serializable;
import java.util.List;


/**
 * A list of {@link MessageItem messages} with a continuation marker.
 * Because this interface represents data in a remote API,
 * all implementations must be serializable.
 */
public interface MessageList extends Serializable
{
  /**
   * Obtains the messages in this list.
   *
   * @return  the read-only list of message items
   */
  public List<? extends MessageItem> getMessages()
    ;


  /**
   * Obtains the continuation marker.
   * The marker is specific to the {@link RemoteMessageBoard board}
   * from which this batch was obtained.
   *
   * @return    an opaque, board-specific marker that indicates where
   *            on the board this list ends
   */
  public String getMarker()
    ;


  /**
   * Indicates whether messages might have been missed.
   * When a message list is fetched from a {@link RemoteMessageBoard},
   * this flag indicates whether messages might have dropped off the board
   * since the end of the preceding list.
   *
   * @return    <code>true</code> if messages might have been missed;
   *            <code>false</code> if this list is consecutive to the
   *            preceding one, or if this is a fresh list
   */
  public boolean isDiscontinuous()
    ;

}
