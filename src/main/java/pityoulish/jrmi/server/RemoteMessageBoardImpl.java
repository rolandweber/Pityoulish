/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import pityoulish.msgboard.MessageBatch;
import pityoulish.msgboard.UserMessageBoard;
import pityoulish.tickets.Ticket;
import pityoulish.tickets.TicketException;
import pityoulish.tickets.TicketManager;

import pityoulish.jrmi.api.APIException;
import pityoulish.jrmi.api.MessageList;
import pityoulish.jrmi.api.RemoteMessageBoard;


/**
 * Default implementation of the {@link RemoteMessageBoard}.
 * The remote message board is backed by a local message board
 * and a local ticket manager.
 */
public class RemoteMessageBoardImpl extends RemoteObject
  implements RemoteMessageBoard
{
  protected final UserMessageBoard msgBoard;

  protected final TicketManager ticketMgr;


  /**
   * Creates a new remote message board.
   *
   * @param umb  the underlying message board
   * @param tm   the underlying ticket manager
   */
  public RemoteMessageBoardImpl(UserMessageBoard umb, TicketManager tm)
  {
    if (umb == null)
       throw new NullPointerException("UserMessageBoard");
    if (tm == null)
       throw new NullPointerException("TicketManager");

    msgBoard  = umb;
    ticketMgr = tm;
  }


  // non-javadoc, see interface
  public MessageList listMessages(int limit, String marker)
    throws APIException // does not throw RemoteException
  {
    //@@@ verify arguments... see issue #11

    MessageBatch mb = msgBoard.listMessages(limit, marker);

    // MessageBatch and other interfaces and classes from pityoulish.msgboard
    // are internal server classes. The data must be converted into classes
    // and interfaces of the remote API, which is available to the client.

    return DataConverter.toMessageList(mb);
  }


  // non-javadoc, see interface
  public void putMessage(String tictok, String text)
    throws APIException // does not throw RemoteException
  {
    //@@@ verify arguments... see issue #11

    try {
      Ticket tick = ticketMgr.lookupTicket(tictok, null);
      if (tick.punch())
       {
         msgBoard.putMessage(tick.getUsername(), text);
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

}
