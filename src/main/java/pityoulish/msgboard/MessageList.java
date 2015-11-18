/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import java.util.List;


/**
 * A list of {@link Message messages} with a continuation marker
 * for a {@link MessageBoard board}.
 */
public interface MessageList extends List<Message>
{
  /**
   * Obtains the continuation marker.
   * The marker can be used to obtain subsequent messages from the
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
  public boolean isDiscontinuos()
    ;

}
