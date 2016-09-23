/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.ServerSocket;


/**
 * Handles requests, on the transport layer.
 * Handler for servicing requests received through socket connections.
 */
public interface SocketHandler
{
  /**
   * Obtains the name of this handler.
   *
   * @return    the name
   */
  public String getName()
    ;


  /**
   * Obtains the underlying request handler.
   *
   * @return    the request handler
   */
  public RequestHandler getRequestHandler()
    ;


  /**
   * Obtains the socket being serviced by this handler.
   *
   * @return    the socket, or
   *            <code>null</code> if this handler is out of service
   */
  public ServerSocket getServerSocket()
    ;


  /**
   * Starts this handler.
   *
   * @param port        the port number, or 0 for some free port
   * @param backlog     the length of the backlog,
   *                    0 or negative for the default length
   *
   * @throws Exception  in case of a problem
   */
  public void startup(int port, int backlog)
    throws Exception
    ;


  /**
   * Shuts down this handler and closes the {@link #getServerSocket socket}.
   *
   * @throws Exception  in case of a problem
   */
  public void shutdown()
    throws Exception
    ;

}
