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
import pityoulish.jrmi.server.RemoteMessageBoardImpl;
import pityoulish.jrmi.server.RemoteTicketIssuerImpl;
import pityoulish.msgboard.MixedMessageBoard;
import pityoulish.msgboard.MixedMessageBoardImpl;
import pityoulish.msgboard.MixedMessageBoardSync;
import pityoulish.sockets.server.SocketHandler;
import pityoulish.tickets.TicketManager;
import pityoulish.tickets.DefaultTicketManager;



/**
 * Main program for the Message Board server running in a pod.
 */
public final class Main
{
  protected final static Logger LOGGER = Log.getPackageLogger(Main.class);


  /** Maximum number of messages the board can hold. */
  protected static int board_capacity = 8;

  /** Name of the env variable for setting {@link #board_capacity}. */
  public final static
    String BOARD_CAPACITY_ENV = "PITYOULISH_BOARD_CAPACITY";

  /**
   * Name of the env variable for setting the hostname in JRMI stubs.
   * The value will be set as system property "java.rmi.server.hostname".
   */
  public final static
    String JRMI_HOSTNAME_ENV = "PITYOULISH_JRMI_HOSTNAME";

  /**
   * Port for the Java RMI Registry in the JRMI exercise.
   * The Java default port is 1099.
   */
  protected static int jrmi_registry_port = 1088;

  /** Name of the env variable for setting {@link #jrmi_registry_port}. */
  public final static
    String JRMI_REGISTRY_PORT_ENV = "PITYOULISH_JRMI_REGISTRY_PORT";

  /**
   * Port for exported objects in the JRMI exercise.
   * Could be the same as {@link #jrmi_registry_port}.
   */
  protected static int jrmi_objects_port = 8108;

  /** Name of the env variable for setting {@link #jrmi_objects_port}. */
  public final static
    String JRMI_OBJECTS_PORT_ENV = "PITYOULISH_JRMI_OBJECTS_PORT";


  /** Port for the Sockets exercise with TLV binary protocol. */
  protected static int sockets_port = 2888;

  /** Name of the env variable for setting {@link #sockets_port}. */
  public final static
    String SOCKETS_PORT_ENV = "PITYOULISH_SOCKETS_PORT";


  /**
   * Main entry point.
   *
   * @param args        ignored
   *
   * @throws Exception  in case of a problem
   */
  public static void main(String[] args)
    throws Exception
  {
    readEnvVars();

    // The server name or IP address has to be set as a system property.
    // Otherwise, stubs will point to some version of 'localhost' and be
    // useless on other machines. Report whether the property is set.
    pityoulish.jrmi.server.Main.checkForHostnameProperty();

    LogConfig.configure(Main.class);
    LOGGER.log(Level.INFO, "starting Message Board server");

    // use a thread-safe wrapper around the unsafe message board impl
    MixedMessageBoard mmb  =
      new MixedMessageBoardSync(new MixedMessageBoardImpl(board_capacity));
    TicketManager tim  = new DefaultTicketManager();

    mmb.putSystemMessage(null, Catalog.SYSMSG_OPEN.lookup());
    mmb.putSystemMessage(null,(Catalog.SYSMSG_CAPACITY_1
                               .format(board_capacity)));


    // initialize Java RMI external interface

    RemoteMessageBoardImpl  rmbi = new RemoteMessageBoardImpl(mmb, tim);
    RemoteTicketIssuerImpl  rtii = new RemoteTicketIssuerImpl(tim, false);

    Remote rmbistub = UnicastRemoteObject.exportObject(rmbi, jrmi_objects_port);
    Remote rtiistub = UnicastRemoteObject.exportObject(rtii, jrmi_objects_port);

    //@@@ NLS light?
    System.out.println(rmbistub);
    System.out.println(rtiistub);

    Registry mainreg =
      pityoulish.jrmi.server.Main.createDefaultRegistry(jrmi_registry_port);
    mainreg.bind(RegistryNames.MESSAGE_BOARD.lookupName, rmbi);
    mainreg.bind(RegistryNames.TICKET_ISSUER.lookupName, rtii);


    // initialize binary protocol external interface

    SocketHandler shandler =
      pityoulish.sockets.server.Main.createTLVSocketHandler(mmb, tim,
                                                            false, false);
    shandler.startup(sockets_port, 0); // adjusting the backlog is pointless

    //@@@ NLS light?
    System.out.println(shandler);
    System.out.println(shandler.getServerSocket());

    // no planned shutdown
  }


  /**
   * Reads the relevant environment variables.
   * The default values of the configuration variables will be overwritten
   * with the setting from the environment variables, if set.
   *
   * @throws Exception   if one of the env vars is invalid
   */
  protected static void readEnvVars()
    throws Exception
  {
    String envvar = null;
    String value = null;
    try {
      envvar = BOARD_CAPACITY_ENV;
      value = System.getenv(envvar);
      if (value != null)
         board_capacity = toInt(value, 1, 8000);

      envvar = JRMI_HOSTNAME_ENV;
      value = System.getenv(envvar);
      if (value != null)
         System.getProperties().setProperty("java.rmi.server.hostname", value);

      envvar = JRMI_REGISTRY_PORT_ENV;
      value = System.getenv(envvar);
      if (value != null)
         jrmi_registry_port = toInt(value, 1025, 65535);

      envvar = JRMI_OBJECTS_PORT_ENV;
      value = System.getenv(envvar);
      if (value != null)
         jrmi_objects_port = toInt(value, 1025, 65535);

      envvar = SOCKETS_PORT_ENV;
      value = System.getenv(envvar);
      if (value != null)
         sockets_port = toInt(value, 1025, 65535);

    } catch (Exception x) {
      LOGGER.log(Level.CONFIG, envvar, x);
      throw new Exception(envvar+": "+x.getMessage(), x);
    }
  }


  /**
   * Converts a string to an integer and checks the range.
   *
   * @param input   the string to parse
   * @param min     the lowest value allowed, inclusive
   * @param max     the highest value allowed, inclusive
   *
   * @return the parsed value within the allowed range
   *
   * @throws Exception   if the input is invalid or out of range
   */
  protected static int toInt(String input, int min, int max)
    throws Exception
  {
    int number = Integer.parseInt(input);
    if (number < min)
       throw new Exception(Catalog.ARG_NUMBER_TOO_LOW_0.format());
    if (number > max)
       throw new Exception(Catalog.ARG_NUMBER_TOO_HIGH_0.format());
    return number;
  }

}
