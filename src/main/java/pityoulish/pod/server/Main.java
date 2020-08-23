/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.pod.server;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import pityoulish.logutil.Log;
import pityoulish.logutil.LogConfig;
import pityoulish.jrmi.api.RegistryNames;
import pityoulish.jrmi.server.Catalog; // for initial messages on board
import pityoulish.jrmi.server.RemoteMessageBoardImpl;
import pityoulish.jrmi.server.RemoteTicketIssuerImpl;
import pityoulish.msgboard.MixedMessageBoard;
import pityoulish.msgboard.MixedMessageBoardImpl;
import pityoulish.sockets.server.SocketHandler;
import pityoulish.tickets.TicketManager;
import pityoulish.tickets.DefaultTicketManager;



/**
 * Main program for the Message Board server running in a pod.
 */
public final class Main
{
  protected final static Logger LOGGER = Log.getPackageLogger(Main.class);

  /**
   * Main entry point.
   *
   * @param args        the command line arguments
   *
   * @throws Exception  in case of a problem
   */
  public static void main(String[] args)
    throws Exception
  {
    int regport = 1088;   // for Java RMI registry, default is 1099
    int rmiport = 8108;   // for Java RMI exported objects
    int tlvport = 2888;   // for Sockets with binary TLV protocol
    int capacity = 8;

    // The server name or IP address has to be set as a system property.
    // Otherwise, stubs will point to some version of 'localhost' and be
    // useless on other machines. Report whether the property is set.
    pityoulish.jrmi.server.Main.checkForHostnameProperty();

    LogConfig.configure(Main.class);
    LOGGER.log(Level.INFO, "starting Message Board server");

    MixedMessageBoard       mmb  = new MixedMessageBoardImpl(capacity);
    TicketManager           tim  = new DefaultTicketManager();
    //@@@ ToDo issue #102: mmb and tim are not thread-safe!

    mmb.putSystemMessage(null, Catalog.SYSMSG_OPEN.lookup());
    mmb.putSystemMessage(null, Catalog.SYSMSG_CAPACITY_1.format(capacity));


    // initialize Java RMI external interface

    RemoteMessageBoardImpl  rmbi = new RemoteMessageBoardImpl(mmb, tim);
    RemoteTicketIssuerImpl  rtii = new RemoteTicketIssuerImpl(tim);

    Remote rmbistub = UnicastRemoteObject.exportObject(rmbi, rmiport);
    Remote rtiistub = UnicastRemoteObject.exportObject(rtii, rmiport);

    //@@@ NLS light?
    System.out.println(rmbistub);
    System.out.println(rtiistub);

    Registry mainreg =
      pityoulish.jrmi.server.Main.createDefaultRegistry(regport);
    mainreg.bind(RegistryNames.MESSAGE_BOARD.lookupName, rmbi);
    mainreg.bind(RegistryNames.TICKET_ISSUER.lookupName, rtii);


    // initialize binary protocol external interface

    SocketHandler shandler =
      pityoulish.sockets.server.Main.createTLVSocketHandler(mmb, tim);
    shandler.startup(tlvport, 0); // adjusting the backlog is pointless

    //@@@ NLS light?
    System.out.println(shandler);
    System.out.println(shandler.getServerSocket());

    // no planned shutdown
  }

}
