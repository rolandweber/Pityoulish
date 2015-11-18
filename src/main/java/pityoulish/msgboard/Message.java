/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * A message from a {@link MessageBoard board}.
 */
public interface Message
{
  /**
   * Obtains the originator of this message.
   * The originator of a regular message is a user;
   * of a system message, a system.
   * To distinguish the two, system names should
   * contain characters that are invalid in user names.
   *
   * @return    the user or system name
   *            designating the originator of this message.
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
   * <p>
   * Representing a timestamp as an instance of <code>java.util.Date</code>
   * has advantages when it has to be formatted for different timezones,
   * or when times have to be compared. Unfortunately, <code>Date</code>
   * objects are modifiable.
   * <a href="http://www.joda.org/joda-time/">Joda-Time
   *         (http://www.joda.org/joda-time/)</a>
   * provides a better date-handling API, which has been adopted
   * for Java SE 8 in the <code>java.time</code> package. However, the
   * code here should not depend on Java 8, nor on an external library.
   * </p>
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
