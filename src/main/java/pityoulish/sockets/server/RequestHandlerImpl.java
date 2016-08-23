/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

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
  public byte[] handle(byte[] reqdata, int start, int end)
   {
     // A missing argument is not a "processing problem" that should be
     // reported as an error response. It's stupidity by the caller.
     if (reqdata == null)
        throw new NullPointerException("reqdata");

     // no exceptions may pass through beyond this point!
     byte[] response = null;
     try {
       MsgBoardRequest mbreq = reqParser.parse(reqdata, start, end);
       response = dispatch(mbreq);
     } catch (Exception x) {
       response = rspBuilder.buildErrorResponse(x);
     }

     return response;
   }


  /**
   * Dispatches a request to the {@link MsgBoardRequestHandler mbrHandler}.
   * Generates the response using the {@link ResponseBuilder rspBuilder}.
   *
   * @param mbreq       the request to process
   *
   * @return    the binary response
   *
   * @throws ProtocolException  in case of a problem
   */
  protected byte[] dispatch(MsgBoardRequest mbreq)
    throws ProtocolException
   {
     byte[] result = null;
     switch (mbreq.getReqType())
      {
       case LIST_MESSAGES: {
         MessageBatch mb = mbrHandler.listMessages(mbreq);
         result = rspBuilder.buildMessageBatch(mb);
       } break;

       case PUT_MESSAGE: {
         String info = mbrHandler.putMessage(mbreq);
         result = rspBuilder.buildInfoResponse(info);
       } break;

       case OBTAIN_TICKET: {
         String tictok = mbrHandler.obtainTicket(mbreq);
         result = rspBuilder.buildTicketGrant(tictok);
       } break;

       case RETURN_TICKET: {
         String info = mbrHandler.returnTicket(mbreq);
         result = rspBuilder.buildInfoResponse(info);
       } break;

       case REPLACE_TICKET: {
         String tictok = mbrHandler.replaceTicket(mbreq);
         result = rspBuilder.buildTicketGrant(tictok);
       } break;

        //@@@ default case might be invoked after adding more request types
      }

     return result;
   }


  // non-javadoc, see interface
  public byte[] buildErrorResponse(String msg)
  {
    return rspBuilder.buildErrorResponse(msg);
  }


  // non-javadoc, see interface
  public byte[] buildErrorResponse(Throwable cause)
  {
    return rspBuilder.buildErrorResponse(cause);
  }

}
