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

/*
import net.dubioso.dhbw.rotox.server.MessageStore;
import net.dubioso.dhbw.rotox.server.DefaultMessageStore;
import net.dubioso.dhbw.rotox.server.TicketManager;
import net.dubioso.dhbw.rotox.server.DefaultTicketManager;
*/


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
    /*    //@@@ get store capacity from command line arguments
    MessageStore   msgstore  = new DefaultMessageStore(35);
    TicketManager  ticketmgr = new DefaultTicketManager();
    RequestHandler rhandler  = new RequestHandler(msgstore, ticketmgr); */
    SocketHandler  shandler  = null; //new SingleThreadHandler(rhandler);
    //@@@ get port number and/or network interface from command line arguments
    shandler.startup(0, 0);

    System.out.println(shandler);
    System.out.println(shandler.getServerSocket());

    Thread.sleep(3600000); // milliseconds
    shandler.shutdown();
  }

}
