/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.server;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import pityoulish.tickets.Ticket;
import pityoulish.tickets.TicketException;
import pityoulish.tickets.TicketManager;

import pityoulish.jrmi.api.DirectMessageOutlet;
import pityoulish.jrmi.api.RemoteOutletManager;
import pityoulish.jrmi.api.APIException;


/**
 * Default implementation of the {@link RemoteOutletManager}.
 * The remote outlet manager is backed by a map
 * and a local ticket manager.
 */
public class RemoteOutletManagerImpl extends RemoteObject
  implements RemoteOutletManager
{
  /**
   * The map from usernames to outlets.
   * Access must be synchronized.
   */
  protected Map<String,DirectMessageOutlet> username2outlet;

  protected final TicketManager ticketMgr;


  /**
   * Creates a new remote outlet manager.
   *
   * @param tm   the underlying ticket manager
   */
  public RemoteOutletManagerImpl(TicketManager tm)
  {
    if (tm == null)
       throw new NullPointerException("TicketManager");

    username2outlet = new HashMap<>();
    ticketMgr = tm;
  }

  // non-javadoc, see interface
  public void publishOutlet(String tictok, DirectMessageOutlet outlet)
    throws APIException
  {
    //@@@ verify first argument... see issue #11

    //@@@ verify outlet... must be pingable

    try {
      // Ticket and TicketManager are thread safe
      Ticket tick = ticketMgr.lookupTicket(tictok, null);
      if (tick.punch())
       {
         synchronized (username2outlet) {
           //@@@ fail if an outlet for the username is already registered?
           username2outlet.put(tick.getUsername(), outlet);
         }
       }
      else
       {
         throw Catalog.TICKET_USED_UP_1.asApiX(tick.getToken());
       }

    } catch (TicketException tx) {
      // clients couldn't deserialize class TicketException
      throw Catalog.TICKET_BAD_2.asApiX(tictok, tx.getLocalizedMessage());
    }
  }


  public void unpublishOutlet(String tictok)
    throws APIException
  {
    //@@@ verify first argument... see issue #11

    try {
      // Ticket and TicketManager are thread safe
      Ticket tick = ticketMgr.lookupTicket(tictok, null);
      if (tick.punch())
       {
         synchronized (username2outlet) {
           //@@@ fail if no outlet for the username is registered?
           username2outlet.remove(tick.getUsername());
         }
       }
      else
       {
         throw Catalog.TICKET_USED_UP_1.asApiX(tick.getToken());
       }

    } catch (TicketException tx) {
      // clients couldn't deserialize class TicketException
      throw Catalog.TICKET_BAD_2.asApiX(tictok, tx.getLocalizedMessage());
    }
  }


  public List<String> listUsernames()
  {
    ArrayList<String> usernames = null;
    synchronized (username2outlet) {
      usernames = new ArrayList<>(username2outlet.keySet());
    }

    // sort now, outside of the synchronized block
    Collections.sort(usernames);

    return usernames;
  }


  public DirectMessageOutlet getOutlet(String username)
    throws APIException
  {
    if (username == null)
       throw new NullPointerException("username");
    // further verification is pointless, the lookup just yields null

    DirectMessageOutlet outlet = null;
    synchronized (username2outlet) {
      outlet = username2outlet.get(username);
    }

    if (outlet == null)
       throw new APIException("no outlet for '"+username+"'"); //@@@ NLS

    return outlet;
  }

}
