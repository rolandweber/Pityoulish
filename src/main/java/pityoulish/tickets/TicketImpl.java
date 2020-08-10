/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

import java.net.InetAddress;


/**
 * Default implementation of a {@link Ticket}.
 */
public class TicketImpl implements Ticket
{
  /** The manager that issued this ticket. */
  protected final TicketManager issuedBy;

  /** The username for which this ticket was issued. */
  protected final String issuedToUsername;

  /** The address for which this ticket was issued. */
  protected final InetAddress issuedToAddress;

  /** The host for which this ticket was issued. */
  protected final String issuedToHost;

  /** The token that represents this ticket. */
  protected final String ticketToken;

  /**
   * The time when this ticket expires.
   * See {@link java.util.Date#getTime Date.getTime()}.
   */
  protected final long expiryTime;

  /** The number of actions remaining. */
  protected int actionsRemaining;


  /**
   * Creates a new ticket.
   *
   * @param creator     the manager creating this ticket
   * @param username    the username
   * @param address     the client address, or <code>null</code>
   * @param host        the client host, or <code>null</code>
   * @param token       the token representing this ticket
   * @param expiry      the time at which this ticket expires. To be compared
   *    with {@link System#currentTimeMillis System.currentTimeMillis()}
   * @param actions     the number of actions to allow with this ticket
   */
  protected TicketImpl(TicketManager creator, String username,
                       InetAddress address, String host,
                       String token, long expiry, int actions)
  {
    if (creator == null)
       throw new NullPointerException("TicketManager");
    if (username == null)
       throw new NullPointerException("username");
    // address and host might be null
    if (token == null)
       throw new NullPointerException("token");

    if (username.length() < 1)
       throw new IllegalArgumentException("empty username");
    if (token.length() < 1)
       throw new IllegalArgumentException("empty token");

    issuedBy = creator;
    issuedToUsername = username;
    issuedToAddress = address;
    issuedToHost = host;
    ticketToken = token;
    expiryTime = expiry;

    actionsRemaining = actions;
  }


  /**
   * Validates this ticket against the provided information.
   * And against the system time, for expiry.
   * If the ticket is not valid, an exception is thrown.
   *
   * @param creator     the ticket manager, mandatory
   * @param username    the username, or <code>null</code> to ignore
   * @param address     the address, or <code>null</code> to ignore
   * @param host        the client host, or <code>null</code> to ignore
   * @param token       the token, mandatory
   *
   * @throws TicketException
   *    if this ticket is not valid in the context of the arguments
   */
  public final void validate(TicketManager creator, String username,
                             InetAddress address, String host, String token)
    throws TicketException
  {
    if (creator == null)
       throw new NullPointerException("TicketManager");
    if (token == null)
       throw new NullPointerException("token");

    // required attribute matches
    if (issuedBy != creator)
       throw new TicketException(Catalog.WRONG_TICKET_MANAGER.lookup());
    if (!ticketToken.equals(token))
       throw new TicketException(Catalog.WRONG_TOKEN.lookup());

    // optional attribute matches
    if ((username != null) && !issuedToUsername.equals(username))
       throw new TicketException(Catalog.WRONG_USERNAME.lookup());
    if ((address != null) && (issuedToAddress != null) &&
        !issuedToAddress.equals(address))
       throw new TicketException(Catalog.WRONG_NETWORK_ADDRESS.lookup());
    if ((host != null) && (issuedToHost != null) &&
        !issuedToHost.equals(host))
       throw new TicketException(Catalog.WRONG_NETWORK_HOST.lookup());

    // validity checks
    if (isExpired())
       throw new TicketException(Catalog.TICKET_EXPIRED.lookup());

    // ok
  }


  /**
   * Checks if this ticket is expired now.
   *
   * @return    <code>true</code> if this ticket is expired,
   *            <code>false</code> otherwise
   */
  public final boolean isExpired()
  {
    final long now = System.currentTimeMillis();
    return isExpired(now);
  }

  /**
   * Checks if this ticket is or was expired at a given time.
   *
   * @param when        the time to check against
   *
   * @return    <code>true</code> if this ticket is expired at the given time,
   *            <code>false</code> otherwise
   */
  public final boolean isExpired(long when)
  {
    return (expiryTime <= when);
  }


  public final String getUsername()
  {
    return issuedToUsername;
  }

  public final InetAddress getAddress()
  {
    return issuedToAddress;
  }

  public final String getHost()
  {
    return issuedToHost;
  }

  public final String getToken()
  {
    return ticketToken;
  }

  public final synchronized boolean punch()
  {
    if (actionsRemaining < 1)
       return false;

    actionsRemaining--;
    return true;
  }


  //@@@ toString
}

