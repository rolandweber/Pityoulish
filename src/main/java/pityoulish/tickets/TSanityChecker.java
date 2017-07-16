/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;


/**
 * Sanity checks for ticket-related parameters from the client side.
 * This interface provides only sanity checks. For example,
 * when a ticket passes, that means the ticket <i>might</i> be valid,
 * not that it is valid.
 */
public interface TSanityChecker<P>
{
  /**
   * Sanity-checks a username.
   *
   * @param username   the username to check
   *
   * @return <code>null</code> if the argument seems valid,
   *         or a problem indicator otherwise
   */
  public P checkUsername(String username)
    ;


  /**
   * Sanity-checks a token for a ticket.
   * This is only a sanity test whether the argument looks like a token
   * that might have come from here. Passing this check does not mean that
   * the argument actually refers to a valid ticket.
   *
   * @param token   the token to check
   *
   * @return <code>null</code> if the argument seems valid,
   *         or a problem indicator otherwise
   */
  public P checkToken(String token)
    ;

}
