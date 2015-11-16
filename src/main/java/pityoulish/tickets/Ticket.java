/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;


/**
 * A ticket.
 * The string {@link #getToken token} is a unique identifier
 * for the ticket. It can be passed to clients and used to
 * retrieve the actual ticket.
 * Tickets are thread safe.
 */
public interface Ticket
{
  /**
   * Obtains the username of this ticket.
   */
  public String getUsername()
    ;


  /**
   * Obtains the token for this ticket.
   */
  public String getToken()
    ;


  /**
   * Checks if this ticket allows another action.
   * If so, the action count is decremented.
   * When the action count reaches 0, no more actions are allowed.
   *
   * @return  <code>true</code> if the action is allowed,
   *          <code>false</code> otherwise
   */
  public boolean punch()
    ;
}

