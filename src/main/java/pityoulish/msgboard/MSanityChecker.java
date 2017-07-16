/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * Sanity checks for message-board-related parameters from the client side.
 * This interface provides only sanity checks. For example,
 * when a marker passes, that means the marker <i>might</i> come from here,
 * not that it did come from here.
 */
public interface MSanityChecker<P>
{
  /**
   * Sanity-checks a marker.
   *
   * @param marker   the marker to check
   *
   * @return <code>null</code> if the argument seems valid,
   *         or a problem description otherwise
   */
  public P checkMarker(String marker)
    ;


  /**
   * Sanity-checks a batch size limit.
   *
   * @param limit   the limit to check
   *
   * @return <code>null</code> if the argument seems valid,
   *         or a problem description otherwise
   */
  public P checkLimit(int limit)
    ;


  /**
   * Sanity-checks a message text.
   *
   * @param text   the message text to check
   *
   * @return <code>null</code> if the argument seems valid,
   *         or a problem description otherwise
   */
  public P checkText(String text)
    ;


  /**
   * Sanity-checks an originator.
   *
   * @param originator   the originator to check
   *
   * @return <code>null</code> if the argument seems valid,
   *         or a problem description otherwise
   */
  public P checkOriginator(String originator)
    ;

}
