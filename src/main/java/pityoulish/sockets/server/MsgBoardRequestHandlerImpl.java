/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import pityoulish.logutil.Log;
import pityoulish.msgboard.MessageBatch;
import pityoulish.msgboard.UserMessageBoard;
import pityoulish.msgboard.MSanityChecker;
import pityoulish.tickets.Ticket;
import pityoulish.tickets.TicketManager;
import pityoulish.tickets.TicketException;
import pityoulish.tickets.TSanityChecker;
import pityoulish.mbserver.StringProblemFactory;
import pityoulish.sockets.server.MsgBoardRequest.ReqType;


/**
 * Default implementation of {@link MsgBoardRequestHandler}.
 */
public class MsgBoardRequestHandlerImpl implements MsgBoardRequestHandler
{
  protected final Logger logger = Log.getPackageLogger(this.getClass());

  protected final UserMessageBoard msgBoard;

  protected final TicketManager ticketMgr;

  protected final MSanityChecker<String> mboardSanityChecker;

  protected final TSanityChecker<String> ticketSanityChecker;


  /**
   * Creates a new application-level request handler.
   *
   * @param umb  the underlying message board
   * @param tm   the underlying ticket manager
   */
  public MsgBoardRequestHandlerImpl(UserMessageBoard umb, TicketManager tm)
  {
    if (umb == null)
       throw new NullPointerException("UserMessageBoard");
    if (tm == null)
       throw new NullPointerException("TicketManager");

    msgBoard  = umb;
    ticketMgr = tm;

    StringProblemFactory spf = new StringProblemFactory();
    mboardSanityChecker = umb.newSanityChecker(spf);
    ticketSanityChecker = tm.newSanityChecker(spf);
  }


  // non-javadoc, see interface
  public MessageBatch listMessages(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
  {
    if (mbreq == null)
       throw new NullPointerException("MsgBoardRequest");
    if (mbreq.getReqType() != ReqType.LIST_MESSAGES)
       throw new IllegalArgumentException
         ("MsgBoardRequest.getReqType()="+mbreq.getReqType());
    if (mbreq.getLimit() == null)
       throw new NullPointerException("MsgBoardRequest.getLimit()");
    // getMarker() is optional, others will be ignored if present
    if (address == null)
       throw new NullPointerException("InetAddress");
    // The value of adress is actually ignored, because no ticket is needed.
    // The address argument is mandatory anyway, might be used someday.

    String problem = null;
    if (mbreq.getMarker() != null) // optional
       problem = mboardSanityChecker.checkMarker(mbreq.getMarker());
    //@@@ sanity check for limit? Mustn't be zero or null.
    //@@@ sanity check for address? Mustn't be null.
    if (problem != null)
       throw Log.log(logger, "listMessages", new ProtocolException(problem));
    //@@@ issue #12: report without exception

    return msgBoard.listMessages(mbreq.getLimit(), mbreq.getMarker());
  }

    
  // non-javadoc, see interface
  public String putMessage(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
  {
    if (mbreq == null)
       throw new NullPointerException("MsgBoardRequest");
    if (mbreq.getReqType() != ReqType.PUT_MESSAGE)
       throw new IllegalArgumentException
         ("MsgBoardRequest.getReqType()="+mbreq.getReqType());
    if (mbreq.getTicket() == null)
       throw new NullPointerException("MsgBoardRequest.getTicket()");
    if (mbreq.getText() == null)
       throw new NullPointerException("MsgBoardRequest.getText()");
    if (address == null)
       throw new NullPointerException("InetAddress");

    String problem = ticketSanityChecker.checkToken(mbreq.getTicket());
    if (problem == null)
       problem = mboardSanityChecker.checkText(mbreq.getText());
    //@@@ sanity check for address? Mustn't be null.
    if (problem != null)
       throw Log.log(logger, "putMessage", new ProtocolException(problem));
    //@@@ issue #12: report without exception

    try {
      Ticket tick = ticketMgr.lookupTicket(mbreq.getTicket(), address);
      if (tick.punch())
       {
         msgBoard.putMessage(tick.getUsername(), mbreq.getText());
         return Catalog.HANDLER_INFO_OK.lookup();
       }
      else
       {
         //@@@ Issue #12: How to return this as plain error, without exception?
         //@@@ Exception classname is automatically added to error response.
         //@@@ Define an extra exception class for "plain error" messages?
         //@@@ Return a status object that indicates Info/Error with msg?
         //@@@ Generalize for all handler methods.
         throw Log.log(logger, "putMessage",
                       Catalog.HANDLER_TICKET_USED_UP_1.asPX(tick.getToken()));
       }

    } catch (TicketException tx) {
      throw Log.log(logger, "putMessage",
                    Catalog.HANDLER_BAD_TICKET_2.asPXwithCause
                    (tx, mbreq.getTicket(), tx.getLocalizedMessage()));
    }
  }

    
  // non-javadoc, see interface
  public MsgBoardResponse<String>
    obtainTicket(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
  {
    if (mbreq == null)
       throw new NullPointerException("MsgBoardRequest");
    if (mbreq.getReqType() != ReqType.OBTAIN_TICKET)
       throw new IllegalArgumentException
         ("MsgBoardRequest.getReqType()="+mbreq.getReqType());
    if (mbreq.getOriginator() == null)
       throw new NullPointerException("MsgBoardRequest.getOriginator()");
    // other values in mbreq will be ignored
    if (address == null)
       throw new NullPointerException("InetAddress");

    // The originator or username must pass multiple sanity checks.
    // The ticket manager checks are stricter. Check with the message board
    // first, to allow for problem reports from both sanity checkers.
    String problem =
      mboardSanityChecker.checkOriginator(mbreq.getOriginator());
    if (problem == null)
       problem = ticketSanityChecker.checkUsername(mbreq.getOriginator());
    //@@@ sanity check for address? Mustn't be null.
    if (problem != null)
     {
       logger.log(Level.WARNING, problem);
       return new MsgBoardResponseImpl.Error(problem);
     }

    try {
      Ticket tick = ticketMgr.obtainTicket(mbreq.getOriginator(), address);

      return new MsgBoardResponseImpl.Ticket(tick.getToken());

    } catch (TicketException tx) {
      throw Log.log(logger, "obtainTicket",
                    Catalog.HANDLER_TICKET_DENIED_2.asPXwithCause
                    (tx, mbreq.getOriginator(), tx.getLocalizedMessage()));
    }
  }


  // non-javadoc, see interface
  public String returnTicket(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
  {
    if (mbreq == null)
       throw new NullPointerException("MsgBoardRequest");
    if (mbreq.getReqType() != ReqType.RETURN_TICKET)
       throw new IllegalArgumentException
         ("MsgBoardRequest.getReqType()="+mbreq.getReqType());
    if (mbreq.getTicket() == null)
       throw new NullPointerException("MsgBoardRequest.getTicket()");
    // other values in mbreq will be ignored
    if (address == null)
       throw new NullPointerException("InetAddress");

    String problem = ticketSanityChecker.checkToken(mbreq.getTicket());
    //@@@ sanity check for address? Mustn't be null.
    if (problem != null)
       throw Log.log(logger, "returnTicket", new ProtocolException(problem));
    //@@@ issue #12: report without exception

    try {
      Ticket tick = ticketMgr.lookupTicket(mbreq.getTicket(), address);
      ticketMgr.returnTicket(tick);

    } catch (TicketException tx) {
      throw Log.log(logger, "returnTicket",
                    Catalog.HANDLER_BAD_TICKET_2.asPXwithCause
                    (tx, mbreq.getTicket(), tx.getLocalizedMessage()));
    }

    return Catalog.HANDLER_INFO_OK.lookup();
  }

    
  // non-javadoc, see interface
  public String replaceTicket(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
  {
    if (mbreq == null)
       throw new NullPointerException("MsgBoardRequest");
    if (mbreq.getReqType() != ReqType.REPLACE_TICKET)
       throw new IllegalArgumentException
         ("MsgBoardRequest.getReqType()="+mbreq.getReqType());
    if (mbreq.getTicket() == null)
       throw new NullPointerException("MsgBoardRequest.getTicket()");
    // other values in mbreq will be ignored
    if (address == null)
       throw new NullPointerException("InetAddress");

    String problem = ticketSanityChecker.checkToken(mbreq.getTicket());
    //@@@ sanity check for address? Mustn't be null.
    if (problem != null)
       throw Log.log(logger, "replaceTicket", new ProtocolException(problem));
    //@@@ issue #12: report without exception

    try {
      Ticket tick = ticketMgr.lookupTicket(mbreq.getTicket(), address);
      ticketMgr.returnTicket(tick);

      tick = ticketMgr.obtainTicket(tick.getUsername(), address);

      return tick.getToken();

    } catch (TicketException tx) {
      throw Log.log(logger, "returnTicket",
                    Catalog.HANDLER_REPLACE_DENIED_2.asPXwithCause
                    (tx, mbreq.getTicket(), tx.getLocalizedMessage()));
    }
  }
    
}
