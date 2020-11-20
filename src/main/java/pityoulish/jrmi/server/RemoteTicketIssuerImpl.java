/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.logging.Logger;

import pityoulish.logutil.Log;
import pityoulish.tickets.Ticket;
import pityoulish.tickets.TicketException;
import pityoulish.tickets.TicketManager;
import pityoulish.tickets.TSanityChecker;

import pityoulish.jrmi.api.APIException;
import pityoulish.jrmi.api.RemoteTicketIssuer;


/**
 * Default implementation of the {@link RemoteTicketIssuer}.
 * The remote ticket issuer is backed by the same local ticket manager
 * as the corresponding remote message board.
 */
public class RemoteTicketIssuerImpl extends RemoteObject
  implements RemoteTicketIssuer
{
  protected final Logger logger = Log.getPackageLogger(this.getClass());

  protected final TicketManager ticketMgr;

  protected final TSanityChecker<APIException> ticketSanityChecker;


  /**
   * Creates a new remote ticket issuer.
   *
   * @param tm   the underlying ticket manager
   */
  public RemoteTicketIssuerImpl(TicketManager tm)
  {
    if (tm == null)
       throw new NullPointerException("TicketManager");

    ticketMgr = tm;

    APIProblemFactory apf = new APIProblemFactory();
    ticketSanityChecker = tm.newSanityChecker(apf);
  }


  // non-javadoc, see interface
  public String obtainTicket(String username)
    throws APIException // does not throw RemoteException
  {
    APIException apix = ticketSanityChecker.checkUsername(username);
    // The ticket manager doesn't know about a message board, so
    // it cannot check the username against restrictions there.
    // Luckily, the ticket manager is stricter anyway.
    if (apix != null)
       throw Catalog.log(logger, "obtainTicket", apix);

    try {
      Ticket tick = ticketMgr.obtainTicket(username, null,
                                           null /*Util.getClientHost()*/);

      System.out.println(Catalog.REPORT_OBTAIN_TICKET_2.format
                         (username, tick.getToken()));

      return tick.getToken();

    } catch (TicketException tx) {
      // clients couldn't deserialize class TicketException
      throw Catalog.log(logger, "obtainTicket", Catalog.TICKET_DENIED_2
                        .asApiX(username, tx.getLocalizedMessage()));
    }
  }


  // non-javadoc, see interface
  public void returnTicket(String tictok)
    throws APIException // does not throw RemoteException
  {
    APIException apix = ticketSanityChecker.checkToken(tictok);
    if (apix != null)
       throw Catalog.log(logger, "returnTicket", apix);

    try {
      Ticket tick = ticketMgr.lookupTicket(tictok, null, Util.getClientHost());
      ticketMgr.returnTicket(tick);

      System.out.println(Catalog.REPORT_RETURN_TICKET_1.format
                         (tick.getUsername()));

    } catch (TicketException tx) {
      // clients couldn't deserialize class TicketException
      throw Catalog.log(logger, "returnTicket", Catalog.TICKET_BAD_2
                        .asApiX(tictok, tx.getLocalizedMessage()));
    }
  }


  // non-javadoc, see interface
  public String replaceTicket(String tictok)
    throws APIException // does not throw RemoteException
  {
    APIException apix = ticketSanityChecker.checkToken(tictok);
    if (apix != null)
       throw Catalog.log(logger, "replaceTicket", apix);

    try {
      Ticket tick = ticketMgr.lookupTicket(tictok, null, Util.getClientHost());
      ticketMgr.returnTicket(tick);

      tick = ticketMgr.obtainTicket(tick.getUsername(), null,
                                    Util.getClientHost());

      System.out.println(Catalog.REPORT_REPLACE_TICKET_2.format
                         (tick.getUsername(), tick.getToken()));

      return tick.getToken();

    } catch (TicketException tx) {
      // clients couldn't deserialize class TicketException
      throw Catalog.log(logger, "replaceTicket",
                        Catalog.TICKET_REPLACE_DENIED_2
                        .asApiX(tictok, tx.getLocalizedMessage()));
    }
  }

}
