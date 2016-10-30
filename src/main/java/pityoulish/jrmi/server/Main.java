/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.server;

import java.util.List;
import java.util.ArrayList;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import pityoulish.jrmi.api.RegistryNames;
import pityoulish.msgboard.MixedMessageBoard;
import pityoulish.msgboard.MixedMessageBoardImpl;
import pityoulish.tickets.TicketManager;
import pityoulish.tickets.DefaultTicketManager;


/**
 * Main program for the Message Board server with Java RMI.
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
    int port = 1088; // default for the exercises (RMI default is 1099)
    int capacity = 8;

    try {
      if (args.length > 0)
         port = Integer.parseInt(args[0]);
      if (args.length > 1)
         capacity = Integer.parseInt(args[1]);
    } catch (NumberFormatException nfx) {
      System.out.println(Catalog.fixEOL(Catalog.USAGE.lookup()));
      System.exit(1);
    }

    // The server name or IP address has to be set as a system property.
    // Otherwise, stubs will point to some version of 'localhost' and be
    // useless on other machines. Report whether the property is set.
    checkForHostnameProperty();


    MixedMessageBoard      mmb  = new MixedMessageBoardImpl(capacity);
    TicketManager          tim  = new DefaultTicketManager();

    RemoteMessageBoardImpl rmbi = new RemoteMessageBoardImpl(mmb, tim);
    RemoteTicketIssuerImpl rtii = new RemoteTicketIssuerImpl(tim);

    // These objects will most likely listen on another port than the registry.
    // It depends on the RMI implementation whether they share the same port.
    Remote rmbistub = UnicastRemoteObject.exportObject(rmbi, 0);
    Remote rtiistub = UnicastRemoteObject.exportObject(rtii, 0);

    //@@@ NLS light?
    System.out.println(rmbistub);
    System.out.println(rtiistub);

    mmb.putSystemMessage(null, Catalog.SYSMSG_OPEN.lookup());
    mmb.putSystemMessage(null, Catalog.SYSMSG_CAPACITY_1.format(capacity));

    Registry mainreg = createDefaultRegistry(port);
    mainreg.bind(RegistryNames.MESSAGE_BOARD.lookupName, rmbi);
    mainreg.bind(RegistryNames.TICKET_ISSUER.lookupName, rtii);


    // If we just return here, the Java program keeps running. There are
    // service threads for the RMI Registry and remotely callable objects.
    // So wait a while, then shut down explicitly.

    Thread.sleep(3600000); // milliseconds
    System.exit(0);
    //@@@ shut down gracefully
  }


  /**
   * Creates an RMI registry running in the current JVM.
   *
   * @param port   the port on which the registry should listen
   *
   * @return the new, local registry
   */
  public static Registry createDefaultRegistry(int port)
    throws RemoteException
  {
    System.out.println(Catalog.REPORT_CREATE_REGISTRY_1.format
                       (String.valueOf(port)));

    return LocateRegistry.createRegistry(port);
  }


  /**
   * Checks for the system property that specifies the hostname for RMI.
   * If that property is not set, stubs created by this JVM point to
   * some version of localhost and are useless on other machines.
   * This method prints a message to System.out, indicating whether the
   * property is set or not.
   *
   * @return    <code>true</code> if the property is set and not empty,
   *            <code>false</code> otherwise
   */
  public final static boolean checkForHostnameProperty()
  {
    final String name = "java.rmi.server.hostname";
    final String value = System.getProperty(name, null);

    final boolean ok = (value != null) && (value.length() > 0);
    if (ok)
     {
       System.out.println(Catalog.REPORT_JRMI_HOSTNAME_1.format
                          (value));
     }
    else
     {
       // Not a fatal problem, still good enough for local development.
       System.out.println(Catalog.REPORT_JRMI_HOSTNAME_PROPERTY_1.format
                          (name));
     }

    return ok;
  }

}
