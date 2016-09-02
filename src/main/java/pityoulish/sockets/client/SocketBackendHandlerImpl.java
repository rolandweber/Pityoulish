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


/**
 * Default implementation of {@link SocketBackendHandler}.
 */
public class SocketBackendHandlerImpl implements SocketBackendHandler
{
  protected String hostName;

  protected int portNumber;


  protected Socket currentSocket;


  // non-javadoc, see interface BackendHandler
  public void describeUsage(Appendable app, String cmd)
    throws IOException
  {
    app.append(Catalog.BACKEND_ARGS_1.format(new Object[]{ cmd }));
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
    //@@@ perform sanity checks? No IP lookup yet.
    hostName = host;

    int port = Integer.parseInt(args[1]);
    if ((port < 1) || (port > 65535))
       throw new IllegalArgumentException("invalid port "+port); //@@@ NLS
    portNumber = port;
  }



  // non-javadoc, see interface SocketBackendHandler
  public String getHostname()
  {
    return hostName;
  }


  // non-javadoc, see interface SocketBackendHandler
  public int getPort()
  {
    return portNumber;
  }



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
    //@@@ to allow for failover, look up all InetAddresses and try one by one?
    return new Socket(hostName, portNumber);
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
    //@@@ to allow for failover, look up all InetAddresses and try one by one?
    InetSocketAddress isa = new InetSocketAddress(hostName, portNumber);
    return SocketChannel.open(isa).socket();
  }

}
