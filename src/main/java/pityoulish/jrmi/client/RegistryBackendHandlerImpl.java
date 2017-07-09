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
import pityoulish.mbclient.HostPortBackendHandlerImpl;
import pityoulish.outtake.Missing;


/**
 * Default implementation of {@link RegistryBackendHandler}.
 */
public class RegistryBackendHandlerImpl extends HostPortBackendHandlerImpl
  implements RegistryBackendHandler
{
  protected Registry rmiRegistry;


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
       // Have a look at the attributes provided by the base class.
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
