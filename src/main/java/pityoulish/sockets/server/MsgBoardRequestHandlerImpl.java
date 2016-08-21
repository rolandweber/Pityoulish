/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.msgboard.MessageBatch;
import pityoulish.msgboard.MessageBoard;
import pityoulish.tickets.TicketManager;
import pityoulish.sockets.server.MsgBoardRequest.ReqType;


/**
 * Default implementation of {@link MsgBoardRequestHandler}.
 */
public class MsgBoardRequestHandlerImpl implements MsgBoardRequestHandler
{
  protected final MessageBoard  msgBoard;

  protected final TicketManager ticketMgr;


  /**
   * Creates a new application-level request handler.
   *
   * @param mb   the underlying message board
   * @param tm   the underlying ticket manager
   */
  public MsgBoardRequestHandlerImpl(MessageBoard mb, TicketManager tm)
  {
    if (mb == null)
       throw new NullPointerException("MessageBoard");
    if (tm == null)
       throw new NullPointerException("TicketManager");

    msgBoard  = mb;
    ticketMgr = tm;
  }


  // non-javadoc, see interface
  public MessageBatch listMessages(MsgBoardRequest mbreq)
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

    return msgBoard.listMessages(mbreq.getLimit(), mbreq.getMarker());
  }

    
  // non-javadoc, see interface
  public String putMessage(MsgBoardRequest mbreq)
    throws ProtocolException
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }

    
  // non-javadoc, see interface
  public String obtainTicket(MsgBoardRequest mbreq)
    throws ProtocolException
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }

    
  // non-javadoc, see interface
  public String returnTicket(MsgBoardRequest mbreq)
    throws ProtocolException
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }

    
  // non-javadoc, see interface
  public String replaceTicket(MsgBoardRequest mbreq)
    throws ProtocolException
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }
    
}
