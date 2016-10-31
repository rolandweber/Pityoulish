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

import pityoulish.jrmi.api.RegistryNames;
import pityoulish.jrmi.api.RemoteOutletManager;

//import pityoulish.outtake.Missing;


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
       // obtain a stub for the RMI registry on the server
       rmiRegistry = LocateRegistry.getRegistry(hostName, portNumber);
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
