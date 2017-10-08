/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import pityoulish.mbclient.HostPortBackendHandlerImpl;
import pityoulish.outtake.Missing;


/**
 * Default implementation of {@link SocketBackendHandler}.
 */
public class SocketBackendHandlerImpl extends HostPortBackendHandlerImpl
  implements SocketBackendHandler
{
  protected Socket currentSocket;


  // non-javadoc, see interface SocketBackendHandler
  public Socket connect(boolean nio)
    throws IOException
  {
    Socket sock = null;
    if (nio)
       sock = connectAsynchronously();
    else
       sock = connectBlocking();

    currentSocket = sock;
    return sock;
  }


  // non-javadoc, see interface SocketBackendHandler
  public Socket getSocket()
  {
    return currentSocket;
  }


  /**
   * Connects to the backend with blocking IO.
   *
   * @return the connected socket
   *
   * @throws IOException   in case of a problem
   */
  protected Socket connectBlocking()
    throws IOException
  {
    Socket sock = null;

    // PYL:keep
    Missing.here("create and connect a socket");
    // Connect to the host and port given on the command line.
    // Look around, both values are readily accessible from here.
    // PYL:cut
    sock = new Socket(hostName, portNumber);
    // PYL:end

    return sock;
  }


  /**
   * Connects to the backend with asynchronous (N)IO.
   *
   * @return the connected socket
   *
   * @throws IOException   in case of a problem
   */
  protected Socket connectAsynchronously()
    throws IOException
  {
    InetSocketAddress isa = new InetSocketAddress(hostName, portNumber);
    return SocketChannel.open(isa).socket();
  }

}
