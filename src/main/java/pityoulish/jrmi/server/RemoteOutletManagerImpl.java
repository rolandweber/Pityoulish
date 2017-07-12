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
import java.util.logging.Logger;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import pityoulish.logutil.Log;
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
  protected final Logger logger = Log.getPackageLogger(this.getClass());

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

    if (outlet == null)
       throw Catalog.log(logger, "publishOutlet",
                         new NullPointerException("DirectMessageOutlet"));

    try {
      outlet.ping(); // make sure the outlet is reachable (wrong hostname?)
    } catch (RemoteException rx) {
      // RemoteException is a standard exception, so it should be
      // safe to wrap it, even when throwing to a remote caller.
       throw Catalog.log(logger, "publishOutlet", Catalog.OUTLET_UNREACHABLE_1
                         .asApiXWithCause(rx, outlet));
    }

    Ticket tick = null;
    try {
      // Ticket and TicketManager are thread safe
      tick = ticketMgr.lookupTicket(tictok, null);
      if (!tick.punch())
         throw Catalog.log(logger, "publishOutlet", Catalog.TICKET_USED_UP_1
                           .asApiX(tick.getToken()));

    } catch (TicketException tx) {
      // clients couldn't deserialize class TicketException
      throw Catalog.log(logger, "publishOutlet", Catalog.TICKET_BAD_2
                        .asApiX(tictok, tx.getLocalizedMessage()));
    }
    // at this point, the ticket is valid and already punched
    final String username = tick.getUsername();

    System.out.println(Catalog.REPORT_PUBLISH_OUTLET_1.format
                       (username));

    // check if the user already has an outlet which is still alive
    DirectMessageOutlet oldlet = null;
    synchronized (username2outlet) {
      oldlet = username2outlet.get(username);
    }
    if (oldlet != null)
     {
       // The ping can take a long time, so it's done outside synchronized {}.
       // In consequence, other calls could set a new outlet, or replace
       // the old one while it's being tested. That must be considered below.
       try {
         oldlet.ping();
         throw Catalog.log(logger, "publishOutlet",
                           Catalog.OUTLET_STILL_ALIVE_1.asApiX(oldlet));

       } catch (RemoteException expected) {
         // proceed, the exception indicates that the old outlet is dead
       } 
     }

    synchronized (username2outlet) {
      DirectMessageOutlet replaced = username2outlet.put(username, outlet);
      if ((replaced != null) && (replaced != oldlet) && (replaced != outlet))
       {
         // A concurrent call published a different outlet for this user.
         // Restore the other outlet and fail this call. This happens inside
         // synchronized {}, so there is no other concurrent modification.
         username2outlet.put(username, replaced);
         throw Catalog.log(logger, "publishOutlet",
                           Catalog.OUTLET_CONCURRENTLY_PUBLISHED_1
                           .asApiX(replaced));
       }
    }
  } // publishOutlet


  public void unpublishOutlet(String tictok)
    throws APIException
  {
    //@@@ verify first argument... see issue #11

    try {
      // Ticket and TicketManager are thread safe
      Ticket tick = ticketMgr.lookupTicket(tictok, null);
      if (tick.punch())
       {
         final String username = tick.getUsername();
         System.out.println(Catalog.REPORT_UNPUBLISH_OUTLET_1.format
                            (username));

         DirectMessageOutlet removed = null;
         synchronized (username2outlet) {
           removed = username2outlet.remove(username);
         }
         if (removed == null)
            throw Catalog.log(logger, "unpublishOutlet", Catalog.OUTLET_NONE_1
                              .asApiX(username));
       }
      else
       {
         throw Catalog.log(logger, "unpublishOutlet", Catalog.TICKET_USED_UP_1
                           .asApiX(tick.getToken()));
       }

    } catch (TicketException tx) {
      // clients couldn't deserialize class TicketException
      throw Catalog.log(logger, "unpublishOutlet", Catalog.TICKET_BAD_2
                        .asApiX(tictok, tx.getLocalizedMessage()));
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
       throw Catalog.log(logger, "getOutlet",
                         new NullPointerException("username"));
    // further verification is pointless, the lookup just yields null

    DirectMessageOutlet outlet = null;
    synchronized (username2outlet) {
      outlet = username2outlet.get(username);
    }

    if (outlet == null)
       throw Catalog.log(logger, "getOutlet", Catalog.OUTLET_NONE_1
                         .asApiX(username));

    return outlet;
  }

}
