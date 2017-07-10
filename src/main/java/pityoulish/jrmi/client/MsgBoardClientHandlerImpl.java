/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;

import pityoulish.jrmi.api.MessageList;
import pityoulish.jrmi.api.RemoteMessageBoard;
import pityoulish.jrmi.api.RemoteTicketIssuer;
import pityoulish.mbclient.MsgBoardClientHandler;
import pityoulish.outtake.Missing;


/**
 * Default implementation of {@link pityoulish.mbclient.MsgBoardClientHandler}
 * with Java RMI.
 */
public class MsgBoardClientHandlerImpl
  implements MsgBoardClientHandler
{
  protected final RegistryBackendHandler regBackend;

  protected final DataFormatter userOutput;


  /**
   * Creates a new client handler implementation.
   *
   * @param rbh   the backend handler
   * @param df    the data formatter for printing results
   */
  public MsgBoardClientHandlerImpl(RegistryBackendHandler rbh,
                                   DataFormatter df)
  {
    if (rbh == null)
       throw new NullPointerException("RegistryBackendHandler");
    if (df == null)
       throw new NullPointerException("DataFormatter");

    regBackend = rbh;
    userOutput = df;
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void listMessages(int limit, String marker)
    throws Exception
  {
    RemoteMessageBoard rmb = regBackend.getRemoteMessageBoard();

    MessageList msglist = rmb.listMessages(limit, marker);

    userOutput.printMessageList(msglist);
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void putMessage(String ticket, String text)
    throws Exception
  {
    RemoteMessageBoard rmb = regBackend.getRemoteMessageBoard();

    rmb.putMessage(ticket, text);

    userOutput.printDone();
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void obtainTicket(String username)
    throws Exception
  {
    RemoteTicketIssuer rti = regBackend.getRemoteTicketIssuer();

    String ticket = rti.obtainTicket(username);

    userOutput.printTicket(ticket);
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void returnTicket(String ticket)
    throws Exception
  {
    RemoteTicketIssuer rti = regBackend.getRemoteTicketIssuer();

    rti.returnTicket(ticket);

    userOutput.printDone();
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void replaceTicket(String ticket)
    throws Exception
  {
    String replacement = null;

    // PYL:keep
    Missing.here("obtain a replacement ticket");
    // PYL:cut
    RemoteTicketIssuer rti = regBackend.getRemoteTicketIssuer();
    replacement = rti.replaceTicket(ticket);
    // PYL:end

    userOutput.printTicket(replacement);
  }

}
