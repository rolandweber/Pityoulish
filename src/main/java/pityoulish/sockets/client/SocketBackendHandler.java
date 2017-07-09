/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.io.IOException;
import java.net.Socket;

import pityoulish.mbclient.HostPortBackendHandler;


/**
 * Backend handler for connecting via sockets.
 */
public interface SocketBackendHandler extends HostPortBackendHandler
{
  /**
   * Connects to the backend.
   *
   * @param nio   <code>true</code> to use asynchronous IO,
   *              <code>false</code> to use blocking IO
   *
   * @return a socket that is connected to the backend
   *
   * @throws IOException   in case of a problem
   */
  public Socket connect(boolean nio)
    throws IOException
    ;


  /**
   * Obtains the socket connected to the backend.
   *
   * @return    the last socket created and returned by {@link #connect},
   *            or <code>null</code> if none was created yet
   */
  public Socket getSocket()
    ;

}

