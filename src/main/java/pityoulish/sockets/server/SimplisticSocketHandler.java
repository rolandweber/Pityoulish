/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
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
      System.out.println(Catalog.NO_DISABLE_SEND_BUFFER_0.format());
      System.out.println(ignore);
    }

    StringBuilder sb = new StringBuilder(120);
    sb.append(handlerName)
      .append(": ")
      .append(Catalog.RECEIVE_FROM_1.format(new Object[]{
            sock.getRemoteSocketAddress()
          }));
    System.out.println(sb);

    // the following calls close the socket on error, so let exceptions pass
    ByteBuffer request  = readRequest(sock);
    ByteBuffer response = handleRequest(sock, request);
    sendResponse(sock, response);

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
   * @param reason      the reason for cancelling this request
   * @param cause       an exception that causes cancelling of this request,
   *                    or <code>null</code>
   * @param sock        the socket from which the cancelled request
   *                    was received, or <code>null</code>
   *
   * @return    an exception with the given <code>reason</code> and
   *            <code>cause</code>, to be thrown by the caller
   */
  public ProtocolException cancelRequest(String reason,
                                         Throwable cause,
                                         Socket sock)
  {
    ProtocolException result = new ProtocolException(reason, cause);
    System.out.println(result.toString());

    sendErrorAndClose(result, sock);

    return result;
  }


  /**
   * Sends an error response and closes the socket.
   * Does nothing if <code>sock</code> is <code>null</code>.
   *
   * @param cause       an exception indicating the cause of the problem
   * @param sock        the socket on which to respond, or <code>null</code>
   */
  public void sendErrorAndClose(Throwable cause, Socket sock)
  {
    if (sock != null)
     {
       try {
         ByteBuffer response = reqHandler.buildErrorResponse(cause);
         sendResponse(sock, response);
       } catch (Exception ignore) {
         cause.addSuppressed(ignore);
       }
       try {
         sock.close();
       } catch (Exception ignore) {
         cause.addSuppressed(ignore);
       }
     }
  } // sendErrorAndClose


  /**
   * Reads a request from a socket and returns it.
   *
   * @param sock        the socket to read the request from
   *
   * @return a byte buffer holding the request, backed by an array.
   *         The request begins at the current position and
   *         extends to the limit of the buffer.
   *         This value is a suitable parameter for {@link #handleRequest}.
   *
   * @throws Exception  in case of a problem
   */
  public ByteBuffer readRequest(Socket sock)
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

    //@@@ This logic is specific to TLVs; also needed by clients... refactor!
    //@@@ Send error response to bogus request, or just drop the connection?

    // Read the first 4 bytes of data, expected in first block of data.
    // Clients that try to send data byte for byte will be kicked out.
    int pos = is.read(data, 0, 4);
    if (pos < 4)
     {
       throw cancelRequest
         (Catalog.RECEIVE_INITIAL_BLOCK_TOO_SMALL_0.format(), null, sock);
     }

    // Requests are in TLV format, see ASN.1 BER
    // byte 1: type of the request
    // byte 2: length of length, value 0x82 indicating 2 bytes for the length
    // byte 3: upper byte of length
    // byte 4: lower byte of length

    if ((data[0] & 0xe0) != 0xe0) // expect bits: 111xxxx
     {
       throw cancelRequest
         (Catalog.RECEIVE_BAD_TYPE_1.format(new Object[]{
             "0x"+Integer.toHexString(data[0] & 0xff)
           }), null, sock);
     }

    if (data[1] != MsgBoardTLV.LENGTH_OF_LENGTH_2)
     {
       throw cancelRequest
         (Catalog.RECEIVE_BAD_LEN_OF_LEN_1.format(new Object[]{
             "0x"+Integer.toHexString(data[1] & 0xff)
           }), null, sock);
     }

    int length = ((data[2] & 0xff)<<8) + (data[3] & 0xff);
    int size = length+4;
    if (size > MAX_REQUEST_SIZE)
     {
       throw cancelRequest
         (Catalog.RECEIVE_TOO_LONG_2.format(new Object[]{
             length, MAX_REQUEST_SIZE
           }), null, sock);
     }

    while (pos < size)
     {
       if (System.currentTimeMillis() > deadline)
        {
          throw cancelRequest
            (Catalog.RECEIVE_DEADLINE_EXPIRED_0.format(), null, sock);
        }

       int count = is.read(data, pos, data.length-pos);
       if (count < 0)
        {
          throw cancelRequest
            (Catalog.RECEIVE_INCOMPLETE_0.format(), null, sock);
        }
       pos += count;
     }

    final long ended = System.currentTimeMillis();
    //System.out.println("received request of "+(length+4)+
    //                    " bytes in "+(ended-started)+" ms");

    ByteBuffer request = ByteBuffer.wrap(data, 0, size);
    return request;

  } // readRequest


  /**
   * Handles a request that has been read from a socket.
   * Calls the {@link RequestHandler} and returns the response.
   *
   * @param sock        the socket from which the request was read
   * @param request     a byte buffer holding the request, backed by an array.
   *                    The request begins at the current position and
   *                    extends to the limit of the buffer.
   *
   * @return a byte buffer holding the response, backed by an array.
   *         The response begins at the current position and
   *         extends to the limit of the buffer.
   *         This value is a suitable parameter for {@link #sendResponse}.
   *
   * @throws Exception  in case of a problem
   */
  public ByteBuffer handleRequest(Socket sock, ByteBuffer request)
    throws Exception
  {
    try {
      // The client machine is (more or less) identified by the host address.
      // The port number there most likely changes for every request.
      InetAddress address = sock.getInetAddress();

      return reqHandler.handle(request.array(),
                               request.position()+request.arrayOffset(),
                               request.limit(),
                               address);

    } catch (Exception x) {
      System.out.println(x.toString());
      sendErrorAndClose(x, sock);
      throw x;
    }

    // in case of success, the socket remains open

  } // handleRequest


  /**
   * Sends a response over a socket.
   * This flushes the output, but does not close the socket on success.
   *
   * @param sock        the socket over which to send
   * @param response    a byte buffer holding the response, backed by an array.
   *                    The response begins at the current position and
   *                    extends to the limit of the buffer.
   *
   * @throws Exception  in case of a problem
   */
  public final void sendResponse(Socket sock, ByteBuffer response)
    throws Exception
  {
    // To make it a bit harder for the clients, don't send everything in a
    // single packet. But at least provide the first tag and length (4 bytes)
    // in the first package. Just to be nice.

    int split = 4 + (int)(Math.random() * (double)(response.remaining()-3));

    try {
      OutputStream os = sock.getOutputStream();
      os.write(response.array(),
               response.position()+response.arrayOffset(),
               split);
      os.flush();
      if (split < response.remaining())
       {
         os.write(response.array(),
                  response.position()+response.arrayOffset()+split,
                  response.remaining()-split);
         os.flush();
       }
    } catch (Exception x) {
      System.out.println(x.toString());
      try {
        sock.close();
      } catch (Exception ignore) {
        x.addSuppressed(ignore);
      }
      throw x;
    }

  } // sendResponse

}
