/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.InetAddress;

import pityoulish.msgboard.MessageBatch;


/**
 * An expositor that prints to <code>System.out</code>.
 */
public class ConsoleExpositorImpl implements Expositor
{

  public void describeRequest(MsgBoardRequest mbreq, InetAddress address)
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


  public void describeMessageBatch(MessageBatch mb)
  {
    System.out.println(Catalog.DESCRIBE_MESSAGE_BATCH_2
                       .format(String.valueOf(mb.getMessages().size()),
                               mb.getMarker()));
    // Using String.valueOf on the number avoids locale-specific formatting.
    // For example, it will print "1234" instead of "1,234" or "1.234".
  }


  public void describeTicketGrant(String tictok)
  {
    System.out.println(Catalog.DESCRIBE_TICKET_GRANT_1.format(tictok));
  }


  public void describeInfoResponse(String info)
  {
    // "OK" is implied
  }


  public void describeException(Throwable axe)
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

}

