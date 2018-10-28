/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

import java.net.InetAddress;

import pityoulish.mbserver.ProblemFactory;


/**
 * Interface of a ticket manager.
 * Allows to obtain, return, verify and use tickets.
 * A ticket has a counter for the number of actions
 * that it allows.
 * A TicketManager is thread safe.
 */
public interface TicketManager
{
  /**
   * Creates a sanity checker for this ticket manager.
   * The returned sanity checker is thread safe if the problem factory is.
   *
   * @param pf   the problem factory to be used by the sanity checker
   *
   * @return a sanity checker for this ticket manager
   *         which uses the argument factory to report problems
   */
  public <P> TSanityChecker<P> newSanityChecker(ProblemFactory<P> pf)
    ;


  /**
   * Obtains a new ticket for a username.
   * Only one ticket will be issued to a user at any one time.
   * If there is a valid ticket for the user, this method fails.
   * There may be other reasons for denying a ticket, like
   * an invalid username or problems with the address.
   *
   * @param username    the name of the user for which to obtain a new ticket
   * @param address     the address from which the ticket is requested,
   *                    or <code>null</code>
   *
   * @return    the new ticket for the user, never <code>null</code>
   *
   * @throws TicketException    if the ticket cannot be granted
   */
  public Ticket obtainTicket(String username, InetAddress address)
    throws TicketException
    ;


  /**
   * Looks up an existing ticket by its token.
   * If the ticket is no longer valid, the lookup fails.
   * If access to the ticket is denied for other reasons,
   * like a mismatching address, the lookup also fails.
   * <br>
   * Note that a ticket for which there are no more actions left
   * can still be valid.
   * Ticket expiry is checked only at the time of the lookup. If the
   * ticket expires afterwards, the action in progress still completes.
   *
   * @param token       the token for the ticket to look up
   * @param address     the address from which the token is provided,
   *                    or <code>null</code>
   *
   * @return    the valid ticket that matches the token,
   *            never <code>null</code>
   *
   * @throws TicketException    if there is no valid ticket for the token,
   *                            or access to that ticket is denied
   */
  public Ticket lookupTicket(String token, InetAddress address)
    throws TicketException
    ;


  /**
   * Returns and invalidates a ticket.
   * This allows a new ticket to be {@link #obtainTicket obtained}.
   *
   * @param tick        the ticket to return
   *
   * @throws TicketException    in case of a problem
   */
  public void returnTicket(Ticket tick)
    throws TicketException
    ;

}
