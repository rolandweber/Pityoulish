/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import pityoulish.logutil.Log;
import pityoulish.logutil.LogConfig;
import pityoulish.msgboard.MixedMessageBoard;
import pityoulish.msgboard.MixedMessageBoardImpl;
import pityoulish.tickets.TicketManager;
import pityoulish.tickets.DefaultTicketManager;


/**
 * Main program for the sample protocol server with sockets.
 * Starts server threads listening on sockets.
 */
public final class Main
{
  protected final static Logger LOGGER = Log.getPackageLogger(Main.class);

  /**
   * Main entry point.
   *
   * @param args        the command line arguments
   */
  public static void main(String[] args)
    throws Exception
  {
    //@@@ get network interface from command line arguments, too?
    //@@@ Use ArgsInterpreter and SingleCommandHandlerBase?
    int port = 2888; // default for the exercises
    int capacity = 8;

    try {
      if (args.length > 0)
         port = Integer.parseInt(args[0]);
      if (args.length > 1)
         capacity = Integer.parseInt(args[1]);
    } catch (NumberFormatException nfx) {
      System.out.println(nfx);
      System.out.println(Catalog.fixEOL(Catalog.USAGE.lookup()));
      System.exit(1);
    }

    LogConfig.configure(Main.class);
    LOGGER.log(Level.INFO, "starting Message Board server");

    MixedMessageBoard      mmb  = new MixedMessageBoardImpl(capacity);
    TicketManager          tm   = new DefaultTicketManager();
    MsgBoardRequestHandler mbrh = new MsgBoardRequestHandlerImpl(mmb, tm);

    RequestParser   reqp = new TLVRequestParserImpl();
    ResponseBuilder rspb = new TLVResponseBuilderImpl();
    RequestHandler  rh   = new RequestHandlerImpl(reqp, mbrh, rspb);

    mmb.putSystemMessage(null, Catalog.SYSMSG_OPEN.lookup());
    mmb.putSystemMessage(null, Catalog.SYSMSG_CAPACITY_1.format(capacity));

    SocketHandler shandler = new SimplisticSocketHandler(rh);
   
    shandler.startup(port, 0); // adjusting the backlog is pointless

    System.out.println(shandler);
    System.out.println(shandler.getServerSocket());

    Thread.sleep(3600000); // milliseconds
    shandler.shutdown();
  }

}
