/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.net.InetAddress;

import pityoulish.logutil.Log;
import pityoulish.mbserver.ProblemFactory;
import pityoulish.mbserver.StringProblemFactory;


/**
 * Default implementation of a {@link TicketManager}.
 * Ticket managers must be thread safe.
 */
public class DefaultTicketManager implements TicketManager
{
  protected final Logger logger = Log.getPackageLogger(this.getClass());

  public final static long TIME_TO_LIVE_MS = 128000; // milliseconds

  /** Characters to be used in the random part of a token. */
  protected final static String RANDOM_TOKEN_CHARS =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._";

  protected final TSanityChecker<String> sanityChecker;

  protected Map<String,TicketImpl>      ticketsByUsername;
  protected Map<String,TicketImpl>      ticketsByToken;
  // the following are for simulated sanity checks...
  protected Map<InetAddress,TicketImpl> ticketsByAddress;
  protected Map<String,TicketImpl>      ticketsByHost;

  //@@@ no housekeeping implemented, expired tickets should get purged


  public DefaultTicketManager()
  {
    sanityChecker = newSanityChecker(new StringProblemFactory());

    ticketsByUsername = new HashMap<String,TicketImpl>();
    ticketsByToken    = new HashMap<String,TicketImpl>();

    ticketsByAddress  = new HashMap<InetAddress,TicketImpl>();
    ticketsByHost     = new HashMap<String,TicketImpl>();
  }


  public <P> TSanityChecker<P> newSanityChecker(ProblemFactory<P> pf)
  {
    return new DefaultTSanityChecker<P>(pf);
  }


  public synchronized Ticket obtainTicket(String username,
                                          InetAddress address,
                                          String host)
    throws TicketException
  {
    String problem = sanityChecker.checkUsername(username);
    if (problem != null)
       throw Log.log(logger, "obtainTicket", new TicketException(problem));

    final long now = System.currentTimeMillis();

    TicketImpl tick = ticketsByUsername.get(username);
    if ((tick != null) && !tick.isExpired(now))
       throw Log.log(logger, "obtainTicket", new TicketException
                     (Catalog.USER_ALREADY_HAS_TICKET_1.format(username)));

    if (address != null)
     {
       // Checking for a network address in this fashion is NOT a reasonable
       // thing to do. Different clients may share the same network address,
       // by means of SOCKS proxies or firewalls with NAT.
       // This is just a placeholder for sanity checks that could be performed
       // by a real application. It also isn't secure in any way, because a
       // machine can have more than one network address, or use SOCKS proxies
       // and firewalls with NAT.

       tick = ticketsByAddress.get(address);
       if ((tick != null) && !tick.isExpired(now))
          throw Log.log(logger, "obtainTicket", new TicketException
                        (Catalog.ADDRESS_ALREADY_HAS_TICKET_1.format(address))
                        );
     }

    if (host != null)
     {
       // see comment on (address != null) above, same applies to hostname
       tick = ticketsByHost.get(host);
       if ((tick != null) && !tick.isExpired(now))
          throw Log.log(logger, "obtainTicket", new TicketException
                        (Catalog.HOST_ALREADY_HAS_TICKET_1.format(host))
                        );
     }

    //@@@ trigger housekeeping if an expired ticket was found?

    final String token = computeToken(username);
    final long  expiry = now + TIME_TO_LIVE_MS; //@@@ randomize?
    final int  actions = 3;                     //@@@ randomize? 2..5
    tick = new TicketImpl(this, username, address, host, token,
                          expiry, actions);
    ticketsByUsername.put(username, tick);
    ticketsByToken.put(token, tick);

    if (address != null)
       ticketsByAddress.put(address, tick);
    if (host != null)
       ticketsByHost.put(host, tick);

    return tick;

  } // obtainTicket


  public synchronized Ticket lookupTicket(String token,
                                          InetAddress address,
                                          String host)
    throws TicketException
  {
    String problem = sanityChecker.checkToken(token);
    if (problem != null)
       throw Log.log(logger, "lookupTicket", new TicketException(problem));

    TicketImpl tick = ticketsByToken.get(token);
    if (tick == null)
       throw Log.log(logger, "lookupTicket", new TicketException
                     (Catalog.TICKET_NOT_FOUND_1.format(token)));

    tick.validate(this, null, address, host, token);

    return tick;

  } // lookupTicket


  public synchronized void returnTicket(Ticket tick)
    throws TicketException
  {
    if (tick == null)
       throw new NullPointerException("Ticket");
    if (!(tick instanceof TicketImpl))
       throw new IllegalArgumentException
         ("wrong class of ticket: "+tick.getClass().getName());

    TicketImpl timp = (TicketImpl) tick;

    // make sure that it is a ticket from here
    timp.validate(this, null, null, null, timp.getToken());

    ticketsByUsername.remove(timp.getUsername());
    ticketsByToken.remove(timp.getToken());

    if (timp.getAddress() != null)
       ticketsByAddress.remove(timp.getAddress());
    if (timp.getHost() != null)
       ticketsByHost.remove(timp.getHost());
  }


  /**
   * Computes a random token for a username.
   *
   * @param username    the username, will be included in the token
   *
   * @return    a token for a new ticket for the argument user
   */
  protected String computeToken(String username)
  {
    // concatenate the username with some random characters
    StringBuilder sb = new StringBuilder(username.length()+6);
    sb.append(username).append('@');

    double random = Math.random();
    // 5*6 = 30 bits of randomness should be available...
    for (int i=0; i<5; i++)
     {
       double which = random * RANDOM_TOKEN_CHARS.length();
       sb.append(RANDOM_TOKEN_CHARS.charAt((int) which));
       random = which - (double)(int)which;
     }

    return sb.toString();
  }

}
