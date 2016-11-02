/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import pityoulish.jrmi.api.RegistryNames;
import pityoulish.jrmi.api.RemoteMessageBoard;
import pityoulish.jrmi.api.RemoteTicketIssuer;
import pityoulish.outtake.Missing;


/**
 * Default implementation of {@link RegistryBackendHandler}.
 */
public class RegistryBackendHandlerImpl implements RegistryBackendHandler
{
  protected String hostName;

  protected int portNumber;

  protected Registry rmiRegistry;


  // non-javadoc, see interface BackendHandler
  public void describeUsage(Appendable app, String cmd)
    throws IOException
  {
    app.append(Catalog.BACKEND_ARGS_1.format(cmd));
  }


  // non-javadoc, see interface BackendHandler
  public int getArgCount()
  {
    return 2;
  }


  // non-javadoc, see interface BackendHandler
  public void setBackend(String... args)
  {
    String host = args[0];
    //@@@ perform sanity checks? Only superficial, no IP lookup yet.
    hostName = host;

    String portarg = args[1];
    try {
      int port = Integer.parseInt(portarg);
      if ((port < 1) || (port > 65535))
         throw new IllegalArgumentException
           (Catalog.CMDLINE_BAD_PORT_1.format(portarg));

      portNumber = port;
    } catch (NumberFormatException nfx) {
      throw new IllegalArgumentException
        (Catalog.CMDLINE_BAD_PORT_1.format(portarg), nfx);
    }
  }



  // non-javadoc, see interface RegistryBackendHandler
  public String getHostname()
  {
    return hostName;
  }


  // non-javadoc, see interface RegistryBackendHandler
  public int getPort()
  {
    return portNumber;
  }


  /**
   * Looks up the RMI registry, if not already done.
   *
   * @return the registry
   *
   * @throws RemoteException    in case of a problem
   */
  public synchronized Registry ensureRegistry()
    throws RemoteException
  {
    if (rmiRegistry == null)
     {
       // PYL:keep
       Missing.here("locate the RMI registry");
       // ...and store it in the rmiRegistry attribute.
       // The other attributes provide all the data you need.
       // PYL:cut
       // obtain a stub for the RMI registry on the server
       rmiRegistry = LocateRegistry.getRegistry(hostName, portNumber);
       // PYL:end
     }

    return rmiRegistry;
  }


  public RemoteMessageBoard getRemoteMessageBoard()
    throws Exception
  {
    return (RemoteMessageBoard)
      ensureRegistry().lookup(RegistryNames.MESSAGE_BOARD.lookupName);
  }


  public RemoteTicketIssuer getRemoteTicketIssuer()
    throws Exception
  {
    RemoteTicketIssuer rti = null;

    // PYL:keep
    Missing.here("look up the RemoteTicketIssuer");
    // ...and return it
    // PYL:cut
    rti = (RemoteTicketIssuer)
      ensureRegistry().lookup(RegistryNames.TICKET_ISSUER.lookupName);
    // PYL:end

    return rti;
  }

}
