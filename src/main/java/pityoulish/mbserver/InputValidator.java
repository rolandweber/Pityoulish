/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.mbserver;



/**
 * Validator for input parameters from the client side.
 * This validator implements sanity checks only.
 * If a username passes, that does not mean that user is known to exist.
 * If a ticket passes, that does not mean the ticket is valid.
 */
public interface InputValidator<P>
{
  /**
   * Validates a marker.
   *
   * @param m   the marker to validate
   *
   * @return <code>null</code> if the argument is valid,
   *         or a problem description otherwise
   */
  public P checkMarker(String m)
    ;


  /**
   * Validates a batch size limit.
   *
   * @param l   the limit to validate
   *
   * @return <code>null</code> if the argument is valid,
   *         or a problem description otherwise
   */
  public P checkLimit(int l)
    ;


  /**
   * Validates a message text.
   *
   * @param t   the message text to validate
   *
   * @return <code>null</code> if the argument is valid,
   *         or a problem description otherwise
   */
  public P checkText(String t)
    ;


  /**
   * Validates a username.
   *
   * @param u   the username to validate
   *
   * @return <code>null</code> if the argument is valid,
   *         or a problem description otherwise
   */
  public P checkUsername(String u)
    ;


  /**
   * Validates a ticket.
   * This is only a sanity test whether the argument looks like a ticket.
   * Passing this check does not mean that the argument actually is a ticket.
   *
   * @param t   the ticket to validate
   *
   * @return <code>null</code> if the argument is valid,
   *         or a problem description otherwise
   */
  public P checkTicket(String t)
    ;

}

