/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

import java.io.IOException;

import pityoulish.mbclient.HostPortBackendHandler;
import pityoulish.jrmi.api.RemoteOutletManager;



/**
 * Backend handler for contacting the RMI Registry.
 */
public interface RegistryBackendHandler extends HostPortBackendHandler
{
  /**
   * Look up the {@link RemoteOutletManager} in the registry.
   *
   * @return    a stub for the Outlet Manager
   *
   * @throws Exception    in case of a problem
   */
  public RemoteOutletManager getRemoteOutletManager()
    throws Exception
    ;

}

