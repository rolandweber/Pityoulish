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
  protected final String handlerName;

  /** The request handler. */
  protected final RequestHandler reqHandler;

  /** The socket. */
  protected ServerSocket srvSocket;


  /**
   * Creates a new socket handler.
   *
   * @param name        the name of this handler
   * @param reqh        the request handler
   */
  protected SocketHandlerBase(String name, RequestHandler reqh)
  {
    if (name == null)
       throw new NullPointerException("name");
    if (reqh == null)
       throw new NullPointerException("RequestHandler");

    handlerName = name;
    reqHandler  = reqh;
  }


  /**
   * Creates a new handler with a default name.
   * The name is generated from the classname.
   *
   * @param reqh        the request handler
   */
  protected SocketHandlerBase(RequestHandler reqh)
  {
    if (reqh == null)
       throw new NullPointerException("RequestHandler");

    String cn   = getClass().getName();
    handlerName = cn.substring(cn.lastIndexOf('.')+1);
    reqHandler  = reqh;
  }



  // non-javadoc, see interface SocketHandler
  public final String getName()
  {
    return handlerName;
  }


  // non-javadoc, see interface SocketHandler
  public final RequestHandler getRequestHandler()
  {
    return reqHandler;
  }


  // non-javadoc, see interface SocketHandler
  public final ServerSocket getServerSocket()
  {
    return srvSocket;
  }


  /**
   * Initializes {@link #srvSocket}.
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
    srvSocket = new ServerSocket(port, backlog);
    srvSocket.setReuseAddress(true);
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

    sb.append(handlerName);
    if (srvSocket != null)
       sb.append('@').append(srvSocket.getLocalPort());

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
