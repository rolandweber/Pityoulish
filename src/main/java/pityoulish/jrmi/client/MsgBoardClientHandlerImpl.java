/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;

import pityoulish.jrmi.api.MessageItem;
import pityoulish.jrmi.api.MessageList;
import pityoulish.jrmi.api.RemoteMessageBoard;
import pityoulish.jrmi.api.RemoteTicketIssuer;


/**
 * Default implementation of {@link MsgBoardClientHandler}.
 */
public class MsgBoardClientHandlerImpl
  implements MsgBoardClientHandler
{
  protected final RegistryBackendHandler regBackend;


  /**
   * Creates a new client handler implementation.
   *
   * @param rbh   the backend handler
   */
  //@@@ pass a call-completion handler (or output formatter? result handler?)
  public MsgBoardClientHandlerImpl(RegistryBackendHandler rbh)
  {
    if (rbh == null)
       throw new NullPointerException("RegistryBackendHandler");

    regBackend = rbh;
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void listMessages(int limit, String marker)
    throws Exception
  {
    RemoteMessageBoard rmb = regBackend.getRemoteMessageBoard();

    //@@@ NLS light
    System.out.println("requesting "+limit+" message(s)" +
                       (marker != null ? " with marker"+marker : ""));
    MessageList msglist = rmb.listMessages(limit, marker);

    //@@@ delegate printing to a call-completion handler
    System.out.println("result: "+msglist);
    //@@@ print marker, print missed indicator
    for (MessageItem msg : msglist.getMessages())
       System.out.println(msg);
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void putMessage(String ticket, String text)
    throws Exception
  {
    RemoteMessageBoard rmb = regBackend.getRemoteMessageBoard();

    rmb.putMessage(ticket, text);
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void obtainTicket(String username)
    throws Exception
  {
    RemoteTicketIssuer rti = regBackend.getRemoteTicketIssuer();

    String ticket = rti.obtainTicket(username);

    //@@@ delegate printing to a call-completion handler
    System.out.println("obtained ticket: "+ticket);
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void returnTicket(String ticket)
    throws Exception
  {
    RemoteTicketIssuer rti = regBackend.getRemoteTicketIssuer();

    rti.returnTicket(ticket);
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void replaceTicket(String ticket)
    throws Exception
  {
    RemoteTicketIssuer rti = regBackend.getRemoteTicketIssuer();

    String replacement = rti.replaceTicket(ticket);

    //@@@ delegate printing to a call-completion handler
    System.out.println("obtained ticket: "+replacement);
  }

}
