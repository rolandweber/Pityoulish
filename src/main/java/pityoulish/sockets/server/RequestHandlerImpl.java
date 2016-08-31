/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import pityoulish.msgboard.MessageBatch;


/**
 * Default implementation of {@link RequestHandler}.
 * It uses:
 * <ol>
 * <li>A {@link RequestParser} to convert binary requests into
 *     {@link MsgBoardRequest} objects.
 * </li>
 * <li>A {@link MsgBoardRequestHandler} to process requests.
 * </li>
 * <li>A {@link ResponseBuilder} to convert results into binary responses.
 * </li>
 * </ol>
 */
public class RequestHandlerImpl implements RequestHandler
{
  protected final RequestParser reqParser;

  protected final MsgBoardRequestHandler mbrHandler;

  protected final ResponseBuilder rspBuilder;


  /**
   * Creates a new request handler.
   *
   * @param rp          the request parser
   * @param mbrh        the application-level request handler
   * @param rb          the response builder
   */
  public RequestHandlerImpl(RequestParser rp,
                            MsgBoardRequestHandler mbrh,
                            ResponseBuilder rb)
   {
     if (rp == null)
        throw new NullPointerException("RequestParser");
     if (mbrh == null)
        throw new NullPointerException("MsgBoardRequestHandler");
     if (rb == null)
        throw new NullPointerException("ResponseBuilder");

     reqParser = rp;
     mbrHandler = mbrh;
     rspBuilder = rb;
   }


  // non-javadoc, see interface
  public ByteBuffer handle(byte[] reqdata, int start, int end,
                           InetAddress address)
   {
     // A missing argument is not a "processing problem" that should be
     // reported as an error response. It's stupidity by the caller.
     if (reqdata == null)
        throw new NullPointerException("reqdata");

     // no exceptions may pass through beyond this point!
     ByteBuffer response = null;
     try {
       MsgBoardRequest mbreq = reqParser.parse(reqdata, start, end);
       response = dispatch(mbreq, address);
     } catch (Exception x) {
       response = rspBuilder.buildErrorResponse(x);
       x.printStackTrace(System.out); //@@@ improve reporting?
     }

     return response;
   }


  /**
   * Dispatches a request to the {@link MsgBoardRequestHandler mbrHandler}.
   * Generates the response using the {@link ResponseBuilder rspBuilder}.
   *
   * @param mbreq       the request to process
   * @param address     the network address of the client
   *
   * @return a buffer containing the response data, backed by an array
   *
   * @throws ProtocolException  in case of a problem
   */
  protected ByteBuffer dispatch(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
   {
     ByteBuffer result = null;
     switch (mbreq.getReqType())
      {
       case LIST_MESSAGES: {
         MessageBatch mb = mbrHandler.listMessages(mbreq, address);
         result = rspBuilder.buildMessageBatch(mb);
       } break;

       case PUT_MESSAGE: {
         String info = mbrHandler.putMessage(mbreq, address);
         result = rspBuilder.buildInfoResponse(info);
       } break;

       case OBTAIN_TICKET: {
         String tictok = mbrHandler.obtainTicket(mbreq, address);
         result = rspBuilder.buildTicketGrant(tictok);
       } break;

       case RETURN_TICKET: {
         String info = mbrHandler.returnTicket(mbreq, address);
         result = rspBuilder.buildInfoResponse(info);
       } break;

       case REPLACE_TICKET: {
         String tictok = mbrHandler.replaceTicket(mbreq, address);
         result = rspBuilder.buildTicketGrant(tictok);
       } break;

        //@@@ default case might be invoked after adding more request types
      }

     return result;
   }


  // non-javadoc, see interface
  public ByteBuffer buildErrorResponse(String msg)
  {
    return rspBuilder.buildErrorResponse(msg);
  }


  // non-javadoc, see interface
  public ByteBuffer buildErrorResponse(Throwable cause)
  {
    return rspBuilder.buildErrorResponse(cause);
  }

}
