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
  protected volatile Thread handlerThread;

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
    if (handlerThread != null)
       throw new IllegalStateException("already started");

    initServerSocket(port, backlog);

    handlerThread = new Thread(this);
    handlerThread.setDaemon(true);
    handlerThread.start();
  }


  /**
   * Shuts down the thread and socket.
   *
   * @throws Exception  in case of a problem
   */
  public synchronized void shutdown()
    throws Exception
  {
    Thread t = handlerThread;
    handlerThread = null; // tell thread to die
    if (t != null)
       t.interrupt();

    if (srvSocket != null)
       srvSocket.close();
  }


  /**
   * Executed by the thread running this handler.
   */
  public void run()
  {
    final Thread runner = Thread.currentThread();

    // access to handlerThread not synchronized, the attribute is volatile
    while (handlerThread == runner)
     {
       try
        {
          acceptAndServeRequest();
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
   * Delegates to {@link #readAndServeRequest}.
   *
   * @throws Exception        in case of a problem
   */
  protected void acceptAndServeRequest()
    throws Exception
  {
    Socket sock = srvSocket.accept();

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
    sb.append(handlerName);
    sb.append(": connection from ")
      .append(sock.getRemoteSocketAddress());
    System.out.println(sb);

    readAndServeRequest(sock);
    // readAndServeRequest closes the socket on error, let exceptions pass

    // single thread + blocking IO means no keep-alive
    sock.close();

  } // acceptAndServeRequest


  /**
   * Cancels a request.
   * The connected socket will be closed. An exception is returned and
   * can be thrown by the caller. Depending on the arguments, this
   * method may try to send an error response back to the client before
   * closing the socket.
   *
   * @param reason      the reason for cancelling this request, or
   *                    <code>null</code> if given by <code>cause</code>
   * @param cause       an exception that causes cancelling of this request,
   *                    or <code>null</code>
   * @param sock        the socket from which the cancelled request
   *                    was received, or <code>null</code>
   *
   * @return    an exception with the given <code>reason</code> and
   *            <code>cause</code>, to be thrown by the caller.
   *            If <code>reason</code> is <code>null</code> and
   *            <code>cause</code> is a {@link ProtocolException},
   *            the return value is <code>cause</code>.
   */
  public ProtocolException cancelRequest(String reason,
                                         Throwable cause,
                                         Socket sock)
  {
    ProtocolException result = null;

    if ((reason == null) && (cause instanceof ProtocolException))
       result = (ProtocolException) cause;
    else
       result = new ProtocolException(reason, cause);

    System.out.println(result.toString());

    if (sock != null)
     {
       try {
         byte[] response = reqHandler.buildErrorResponse(result);
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
   * Reads a request from a socket and serves it.
   * Calls the {@link RequestHandler}, followed by {@link #sendResponse}.
   *
   * @param sock        the socket to read the request from
   *
   * @throws Exception  in case of a problem
   */
  public void readAndServeRequest(Socket sock)
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
         ("did not receive minimum data in first block", null, sock);
     }

    // Requests are in TLV format, see ASN.1 BER
    // byte 1: type of the request
    // byte 2: length of length, value 0x82 indicating 2 bytes for the length
    // byte 3: upper byte of length
    // byte 4: lower byte of length

    if ((data[0] & 0xe0) != 0xe0) // expect bits: 111xxxx
     {
       throw cancelRequest
         ("unexpected type "+data[0], null, sock);
     }

    if (data[1] != MsgBoardTLV.LENGTH_OF_LENGTH_2)
     {
       throw cancelRequest
         ("unexpected length of length "+data[1], null, sock);
     }

    int length = ((data[2] & 0xff)<<8) + (data[3] & 0xff);
    int size = length+4;
    if (size > MAX_REQUEST_SIZE)
     {
       throw cancelRequest
         ("request too long: "+length, null, sock);
     }

    while (pos < size)
     {
       if (System.currentTimeMillis() > deadline)
        {
          throw cancelRequest
            ("deadline for receiving expired", null, sock);
        }

       int count = is.read(data, pos, data.length-pos);
       if (count < 0)
        {
          throw cancelRequest
            ("unexpected end of data", null, sock);
        }
       pos += count;
     }

    final long ended = System.currentTimeMillis();
    //System.out.println("received request of "+(length+4)+
    //                    " bytes in "+(ended-started)+" ms");

    //@@@ Instead of calling serveRequest, return a java.nio.ByteBuffer
    //@@@ and let the caller invoke the subsequent method to process it?

    serveRequest(sock, data, size);

  } // readAndServeRequest


  /**
   * Serves a request that has been read from a socket.
   * Calls the {@link RequestHandler}, then delegates to {@link #sendResponse}.
   *
   * @param sock        the socket from which the request was read
   * @param request     an array holding the request, starting at index 0
   * @param length      the length of the request, in bytes.
   *                    That's the same as the index after the last byte.
   *
   * @throws Exception  in case of a problem
   */
  public void serveRequest(Socket sock, byte[] request, int length)
    throws Exception
  {
    try {
      // The client machine is (more or less) identified by the host address.
      // The port number there most likely changes for every request.
      InetAddress address = sock.getInetAddress();

      byte[] response = reqHandler.handle(request, 0, length);
      sendResponse(sock, response);

    } catch (ProtocolException px) {
      throw cancelRequest(null, px, sock);
    }

    // in case of success, the socket remains open

  } // serveRequest


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
