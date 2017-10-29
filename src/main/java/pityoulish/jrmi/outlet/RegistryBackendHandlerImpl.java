/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import pityoulish.mbclient.HostPortBackendHandlerImpl;
import pityoulish.jrmi.api.RegistryNames;
import pityoulish.jrmi.api.RemoteOutletManager;
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
       Missing.here("locate the RMI registry running on the server");
       // ...and store it in the rmiRegistry attribute.
       // That's the same piece of code which is missing from the client, too.
       // You didn't think you could copy it from here, did you?
       // PYL:cut
       // obtain a stub for the RMI registry on the server
       rmiRegistry = LocateRegistry.getRegistry(hostName, portNumber);
       // PYL:end
     }

    return rmiRegistry;
  }


  public RemoteOutletManager getRemoteOutletManager()
    throws Exception
  {
    return (RemoteOutletManager)
      ensureRegistry().lookup(RegistryNames.OUTLET_MANAGER.lookupName);
  }

}
