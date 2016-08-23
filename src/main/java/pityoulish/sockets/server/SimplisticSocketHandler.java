/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//@@@ some TLV parsing is duplicated here... refactor to avoid that!
import pityoulish.sockets.tlv.MsgBoardTLV;


/**
 * Simplistic socket IO handler for the example protocol.
 * Uses blocking IO and a single thread to sequentially serve requests.
 * Denial of service is somewhat limited by timeouts.
 */
public class SimplisticSocketHandler extends SocketHandlerBase
  implements Runnable
{
  /** The thread executing this handler. */
  protected volatile Thread handler_thread;

  public final static int  MAX_REQUEST_SIZE = 1024; // bytes
  public final static int  RCV_SO_TIMEOUT   =  666; // milliseconds
  public final static long RCV_DEADLINE     = 2222; // milliseconds


  /**
   * Creates a new single-threaded handler for incoming requests.
   *
   * @param reqh        the underlying request handler
   */
  public SimplisticSocketHandler(RequestHandler reqh)
  {
    super(reqh);
  }



  /**
   * Creates a socket and starts a thread servicing it.
   *
   * @param port        the port number, or 0 for some free port
   * @param backlog     the length of the backlog,
   *                    0 or negative for the default length
   *
   * @throws Exception  in case of a problem
   */
  public synchronized void startup(int port, int backlog)
    throws Exception
  {
    if (handler_thread != null)
       throw new IllegalStateException("already started");

    initServerSocket(port, backlog);

    handler_thread = new Thread(this);
    handler_thread.setDaemon(true);
    handler_thread.start();
  }


  /**
   * Shuts down the thread and socket.
   *
   * @throws Exception  in case of a problem
   */
  public synchronized void shutdown()
    throws Exception
  {
    Thread t = handler_thread;
    handler_thread = null; // tell thread to die
    if (t != null)
       t.interrupt();

    if (server_socket != null)
       server_socket.close();
  }


  /**
   * Executed by the thread running this handler.
   */
  public void run()
  {
    final Thread runner = Thread.currentThread();

    // access to handler_thread not synchronized, the attribute is volatile
    while (handler_thread == runner)
     {
       try
        {
          serveRequest();
        }
       catch (Exception x)
        {
          x.printStackTrace(System.out);
        }
     }
  }


  /**
   * Accepts the next connection, reads a request and serves it.
   * Repeatedly called by {@link #run}.
   * Blocks until there is a connection or a problem.
   *
   * @throws Exception        in case of a problem
   */
  protected void serveRequest()
    throws Exception
  {
    Socket sock = server_socket.accept();

    try {
      // Of course it makes no sense to work without a send buffer. And
      // it's only a hint, which might be ignored by the implementation.
      // The idea is to send the response in pieces, to make sure that
      // the client reads everything and not just the first packet.
      // Calling flush() on the OutputStream isn't enough to achieve that.
      sock.setSendBufferSize(4);
    } catch (Exception ignore) {
      System.out.println("failed to disabled send buffer... "+ignore);
    }

    StringBuilder sb = new StringBuilder(120);
    sb.append(handler_name);
    sb.append(": connection from ")
      .append(sock.getRemoteSocketAddress());
    System.out.println(sb);

    byte[] request = readRequest(sock);
    // readRequest closes the socket on error, let exceptions pass

    try {
      // The client machine is (more or less) identified by the host address.
      // The port number there most likely changes for every request.
      InetAddress address = sock.getInetAddress();

      byte[] response = request_handler.handle(request, 0, request.length);
      sendResponse(sock, response);

    } catch (ProtocolException px) {
      byte[] response = request_handler.buildErrorResponse(px);
      sendResponse(sock, response);
      throw px;

    } finally {
      sock.close();
    }

    //@@@ perform random act of weirdness occasionally, based on elapsed time,
    //@@@ number of requests, random decision, or a combination thereof

  } // serveRequest


  /**
   * Cancels a request.
   * The connected socket will be closed. An exception is returned and
   * can be thrown by the caller. Depending on the arguments, this
   * method may try to send an error response back to the client before
   * closing the socket.
   *
   * @param reason      the reason for cancelling this request
   * @param cause       an exception that causes cancelling of this request,
   *                    or <code>null</code>
   * @param sock        the socket from which the cancelled request
   *                    was received, or <code>null</code>
   * @param reply       <code>true</code> to send an error response,
   *                    <code>false</code> to just close the socket
   *
   * @return    an exception with the given <code>reason</code> and
   *            <code>cause</code>, to be thrown by the caller
   */
  public ProtocolException cancelRequest(String reason,
                                         Throwable cause,
                                         Socket sock,
                                         boolean reply)
  {
    ProtocolException result = new ProtocolException(reason, cause);
    System.out.println(result.toString());

    if (sock != null)
     {
       try {
         byte[] response = request_handler.buildErrorResponse(result);
         sendResponse(sock, response);
       } catch (Exception ignore) {
         // result.addSuppressed(ignore); // requires Java 7
       }
       try {
         sock.close();
       } catch (Exception ignore) {
         // result.addSuppressed(ignore); // requires Java 7
       }
     }
    return result;

  } // cancelRequest


  /**
   * Reads a request from a socket.
   *
   * @param sock        the socket to read the request from
   *
   * @return    the request data
   *
   * @throws Exception  in case of a problem
   */
  public byte[] readRequest(Socket sock)
    throws Exception
  {
    // to avoid permanent blocking by misbehaving clients:
    // - set a timeout on the socket
    // - track how long it takes to receive the request, cancel if necessary
    //   The client could send byte by byte to avoid timeouts.

    sock.setSoTimeout(RCV_SO_TIMEOUT);
    final long started = System.currentTimeMillis();
    final long deadline = started + RCV_DEADLINE;

    byte[] data = new byte[MAX_REQUEST_SIZE];

    InputStream is = sock.getInputStream();

    //@@@ This logic is specific to TLVs... move it into RequestHandler!
    //@@@ NLS light

    // Read the first 4 bytes of data, expected in first block of data.
    // Clients that try to send data byte for byte will be kicked out.
    int pos = is.read(data, 0, 4);
    if (pos < 4)
     {
       throw cancelRequest
         ("did not receive minimum data in first block", null, sock, false);
     }

    // Requests are in TLV format, see ASN.1 BER
    // byte 1: type of the request
    // byte 2: length of length, value 0x82 indicating 2 bytes for the length
    // byte 3: upper byte of length
    // byte 4: lower byte of length

    if ((data[0] & 0xe0) != 0xe0) // expect bits: 111xxxx
     {
       throw cancelRequest
         ("unexpected type "+data[0], null, sock, false);
     }

    if (data[1] != MsgBoardTLV.LENGTH_OF_LENGTH_2)
     {
       throw cancelRequest
         ("unexpected length of length "+data[1], null, sock, false);
     }

    int length = ((data[2] & 0xff)<<8) + (data[3] & 0xff);
    int size = length+4;
    if (size > MAX_REQUEST_SIZE)
     {
       throw cancelRequest
         ("request too long: "+length, null, sock, false);
     }

    while (pos < size)
     {
       if (System.currentTimeMillis() > deadline)
        {
          throw cancelRequest
            ("deadline for receiving expired", null, sock, false);
        }

       int count = is.read(data, pos, data.length-pos);
       if (count < 0)
        {
          throw cancelRequest
            ("unexpected end of data", null, sock, false);
        }
       pos += count;
     }

    final long ended = System.currentTimeMillis();
    //System.out.println("received request of "+(length+4)+
    //                    " bytes in "+(ended-started)+" ms");

    //@@@ refactor this method or class to avoid copying of the request!
    //@@@ can only return one object, not array and end position at same time
    if (data.length > size)
     {
       byte[] shrink = new byte[size];
       System.arraycopy(data, 0, shrink, 0, size);
       data = shrink;
     }

    return data;

  } // readRequest


  /**
   * Sends a response over a socket.
   * This flushes the output, but does not close the socket.
   *
   * @param sock        the socket over which to send
   * @param response    the response data to be sent
   *
   * @throws Exception  in case of a problem
   */
  public final void sendResponse(Socket sock, byte[] response)
    throws Exception
  {
    // To make it a bit harder for the clients, don't send everything in a
    // single packet. But at least provide the first tag and length (4 bytes)
    // in the first package. Just to be nice.

    int split = 4 + (int)(Math.random() * (double)(response.length-3));
    //System.out.println("response length "+response.length+", splitting at "+split);

    OutputStream os = sock.getOutputStream();
    os.write(response, 0, split);
    os.flush();
    if (split < response.length)
     {
       os.write(response, split, response.length-split);
       os.flush();
     }
  } // sendResponse

}
