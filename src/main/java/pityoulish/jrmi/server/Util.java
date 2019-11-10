/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.server;

import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;


/**
 * Server-side utility methods.
 */
public class Util
{
  /** Disabled default constructor. */
  private Util()
  {
    throw new UnsupportedOperationException("no instances of this class");
  }


  /**
   * Determines the host from which the remote call initiates, if available.
   *
   * @return the calling host, or <code>null</code>
   */
  public static String getClientHost()
  {
    String host = null;

    try {
      host = RemoteServer.getClientHost();
    } catch (ServerNotActiveException snax) {
      // ignore
    }

    return host;
  }

} 
