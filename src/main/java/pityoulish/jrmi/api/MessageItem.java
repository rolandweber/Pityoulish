/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.api;

import java.io.Serializable;


/**
 * An item in a {@link MessageList}, in other words, a message.
 * Because this interface represents data in a remote API,
 * all implementations must be serializable.
 */
public interface MessageItem extends Serializable
{
  /**
   * Obtains the originator of this message.
   *
   * @return    the originator of this message.
   *            Never <code>null</code>.
   */
  public String getOriginator()
    ;


  /**
   * Obtains the timestamp of this message.
   * For practical reasons, the timestamp is represented as a string.
   * It uses an ISO date-time format with time zone UTC, for example:
   * "<code>2015-11-18T21:09:33Z</code>".
   * The precision is limited to seconds.
   *
   * @return    the time at which this message was put on the board.
   *            Never <code>null</code>.
   */
  public String getTimestamp()
    ;


  /**
   * Obtains the content of this message.
   *
   * @return    the message text.
   *            Never <code>null</code>.
   */
  public String getText()
    ;
}
