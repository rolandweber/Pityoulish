/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Base class for implementing {@link SocketHandler}.
 */
public abstract class SocketHandlerBase
  implements SocketHandler
{
  /** The name of this handler. */
  protected final String handler_name;

  /** The socket. */
  protected ServerSocket server_socket;


  /**
   * Creates a new handler with the given name.
   *
   * @param name        the name of this handler
   */
  protected SocketHandlerBase(String name)
  {
    if (name == null)
       throw new NullPointerException("name");
    handler_name = name;
  }


  /**
   * Creates a new handler with a default name.
   * The name is generated from the classname.
   */
  protected SocketHandlerBase()
  {
    String cn = getClass().getName();
    handler_name = cn.substring(cn.lastIndexOf('.')+1);
  }



  // non-javadoc, see interface SocketHandler
  public final String getName()
  {
    return handler_name;
  }


  // non-javadoc, see interface SocketHandler
  public final ServerSocket getServerSocket()
  {
    return server_socket;
  }


  /**
   * Initializes {@link #server_socket}.
   * Derived classes may as well initialize that variable themselves,
   * without calling this method here.
   *
   * @param port        the port number, or 0 for some free port
   * @param backlog     the length of the backlog,
   *                    0 or negative for the default length
   *
   * @throws IOException        in case of a problem
   */
  protected void initServerSocket(int port, int backlog)
    throws IOException
  {
    server_socket = new ServerSocket(port, backlog);
    server_socket.setReuseAddress(true);
  }


  /**
   * Generates a description of this handler.
   * Derived classes can enhance the description by overriding
   * {@link #toString(StringBuilder)}.
   *
   * @return    a human-readable description of this object
   */
  public final String toString()
  {
    StringBuilder sb = new StringBuilder(80);

    sb.append(handler_name);
    if (server_socket != null)
       sb.append('@').append(server_socket.getLocalPort());

    sb.append(':').append(getClass().getName());

    toString(sb);

    return sb.toString();
  }


  /**
   * Hook for changing the output of {@link #toString()}.
   *
   * @param sb    a buffer holding the default output of {@link #toString()}
   */
  protected void toString(StringBuilder sb)
  {
    // do nothing
  }

}
