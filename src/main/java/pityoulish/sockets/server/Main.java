/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.util.List;
import java.util.ArrayList;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import pityoulish.msgboard.UserMessageBoard;
import pityoulish.msgboard.MixedMessageBoardImpl;
import pityoulish.tickets.TicketManager;
import pityoulish.tickets.DefaultTicketManager;


/**
 * Main program for the sample protocol server with sockets.
 * Starts server threads listening on sockets.
 */
public final class Main
{
  /**
   * Main entry point.
   *
   * @param args        the command line arguments
   */
  public static void main(String[] args)
    throws Exception
  {
    //@@@ get board capacity from command line arguments
    UserMessageBoard       umb  = new MixedMessageBoardImpl(8);
    TicketManager          tm   = new DefaultTicketManager();
    MsgBoardRequestHandler mbrh = new MsgBoardRequestHandlerImpl(umb, tm);

    RequestParser   reqp = new TLVRequestParserImpl();
    ResponseBuilder rspb = new TLVResponseBuilderImpl();
    RequestHandler  rh   = new RequestHandlerImpl(reqp, mbrh, rspb);

    SocketHandler  shandler  = new SimplisticSocketHandler(rh);
    //@@@ get port number and/or network interface from command line arguments
    shandler.startup(0, 0);

    System.out.println(shandler);
    System.out.println(shandler.getServerSocket());

    Thread.sleep(3600000); // milliseconds
    shandler.shutdown();
  }

}
