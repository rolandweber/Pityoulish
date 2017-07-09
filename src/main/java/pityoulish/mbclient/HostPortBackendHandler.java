/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.mbclient;

import pityoulish.cmdline.BackendHandler;


/**
 * Backend handler for hostname and port arguments.
 */
public interface HostPortBackendHandler extends BackendHandler
{
  /**
   * Obtains the hostname to connect to.
   *
   * @return the hostname, or <code>null</code> if not yet provided
   */
  public String getHostname()
    ;


  /**
   * Obtains the port to connect to.
   *
   * @return the port number, or 0 if not yet provided
   */
  public int getPort()
    ;

}

