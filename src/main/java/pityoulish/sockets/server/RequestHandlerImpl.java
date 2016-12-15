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
  public ByteBuffer handle(ByteBuffer request, InetAddress address)
   {
     // A missing argument is not a "processing problem" that should be
     // reported as an error response. It's stupidity by the caller.
     if (request == null)
        throw new NullPointerException("request");

     // no exceptions may pass through beyond this point!
     ByteBuffer response = null;
     try {
       MsgBoardRequest mbreq = reqParser.parse(request);
       response = dispatch(mbreq, address);

     } catch (Exception x) {
       response = rspBuilder.buildErrorResponse(x);
       describeException(x);
       // x.printStackTrace(System.out); //@@@ trace to a logfile
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
     describeRequest(mbreq, address);

     ByteBuffer result = null;
     switch (mbreq.getReqType())
      {
       case LIST_MESSAGES: {
         MessageBatch mb = mbrHandler.listMessages(mbreq, address);
         describeMessageBatch(mb);
         result = rspBuilder.buildMessageBatch(mb);
       } break;

       case PUT_MESSAGE: {
         String info = mbrHandler.putMessage(mbreq, address);
         describeInfoResponse(info);
         result = rspBuilder.buildInfoResponse(info);
       } break;

       case OBTAIN_TICKET: {
         String tictok = mbrHandler.obtainTicket(mbreq, address);
         describeTicketGrant(tictok);
         result = rspBuilder.buildTicketGrant(tictok);
       } break;

       case RETURN_TICKET: {
         String info = mbrHandler.returnTicket(mbreq, address);
         describeInfoResponse(info);
         result = rspBuilder.buildInfoResponse(info);
       } break;

       case REPLACE_TICKET: {
         String tictok = mbrHandler.replaceTicket(mbreq, address);
         describeTicketGrant(tictok);
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


  //@@@ move the descriptions to a dedicated helper?
  //@@@ would improve symmetry with the client side

  /**
   * Describes an exception.
   * The description is printed to <code>System.out</code>.
   */
  protected void describeException(Throwable axe)
  {
    if (axe instanceof ProtocolException)
     {
       // always contains a descriptive error message
       System.out.println(axe.getLocalizedMessage()); // no trace
     }
    else
     {
       // something unexpected
       System.out.println(axe.toString()); // no trace
     }
  }


  /**
   * Describes an incoming request.
   * The description is printed to <code>System.out</code>.
   *
   * @param mbreq       the request to describe
   * @param address     the network address of the client
   */
  protected void describeRequest(MsgBoardRequest mbreq, InetAddress address)
  {
    // For the classroom exercise, we don't need an exhaustive description
    // of the request. Students should see that their request is coming in.
    // And the messages put on the board should be visible to students and
    // the instructor alike. For tickets, the returned ones are of interest.
    // Just print all that get sent. Tickets conveniently include a username.
    System.out.println(Catalog.DESCRIBE_REQUEST_TYPE_ADDR_2
                       .format(mbreq.getReqType(), address));

    if (mbreq.getText() != null)
       System.out.println(Catalog.DESCRIBE_REQUEST_TICKET_TEXT_2
                          .format(mbreq.getTicket(), mbreq.getText()));
    else if (mbreq.getTicket() != null)
       System.out.println(Catalog.DESCRIBE_REQUEST_TICKET_1
                          .format(mbreq.getTicket()));
  }


  /**
   * Describes a message batch.
   * The description is printed to <code>System.out</code>.
   */
  protected void describeMessageBatch(MessageBatch mb)
  {
    System.out.println(Catalog.DESCRIBE_MESSAGE_BATCH_2
                       .format(String.valueOf(mb.getMessages().size()),
                               mb.getMarker()));
    // Using String.valueOf on the number avoids locale-specific formatting.
    // For example, it will print "1234" instead of "1,234" or "1.234".
  }


  /**
   * Describes a granted ticket.
   * The description is printed to <code>System.out</code>.
   */
  protected void describeTicketGrant(String tictok)
  {
    System.out.println(Catalog.DESCRIBE_TICKET_GRANT_1.format(tictok));
  }


  /**
   * Describes an informational response.
   * The description is printed to <code>System.out</code>.
   */
  protected void describeInfoResponse(String info)
  {
    // "OK" is implied
  }

}
