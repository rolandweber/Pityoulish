/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.InetAddress;

import pityoulish.msgboard.MessageBatch;
import pityoulish.msgboard.MixedMessageBoardImpl; //@@@ use JMockit instead
import pityoulish.tickets.DefaultTicketManager; //@@@ use JMockit instead
import pityoulish.sockets.server.MsgBoardRequest.ReqType;

import org.junit.*;
import static org.junit.Assert.*;

public class MsgBoardRequestHandlerImplTest
{
  private final static InetAddress ADDRESS;
  static {
    try {
      ADDRESS = InetAddress.getLocalHost();
    } catch (Exception x) {
      throw new ExceptionInInitializerError(x);
    }
  }


  @Test public void listMessages_OK()
    throws Exception
  {
    MsgBoardRequest mbreq = MsgBoardRequestImpl.newListMessages(1, null);

    //@@@ use JMockit for prereq objects, instead of creating default impls
    MsgBoardRequestHandler mbrh = new MsgBoardRequestHandlerImpl
      (new MixedMessageBoardImpl(8), new DefaultTicketManager());


    MessageBatch result = mbrh.listMessages(mbreq, ADDRESS);

    assertNotNull("no result", result);
    assertEquals("wrong number of messages", 0, result.getMessages().size());
    assertNotNull("no marker", result.getMarker());
    assertEquals("wrong discontinuity", false, result.isDiscontinuous());

    //@@@ use JMockit to verify which methods of prereq objects were called
    //@@@ make sure the correct limit (and marker) are passed
  }


  @Test public void listMessages_bad()
    throws Exception
  {
    //@@@ use JMockit for prereq objects, instead of creating default impls
    MsgBoardRequestHandler mbrh = new MsgBoardRequestHandlerImpl
      (new MixedMessageBoardImpl(8), new DefaultTicketManager());


    try {
      MsgBoardRequest mbreq = null;
      MessageBatch result = mbrh.listMessages(mbreq, ADDRESS);
      fail("missing request not detected: " + result);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      MsgBoardRequest mbreq = MsgBoardRequestImpl.newObtainTicket("un1t");
      MessageBatch result = mbrh.listMessages(mbreq, ADDRESS);
      fail("wrong request type detected: " + result);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      MsgBoardRequest mbreq = new MsgBoardRequestImpl
        (ReqType.LIST_MESSAGES, null, null, null, null, null);
      MessageBatch result = mbrh.listMessages(mbreq, ADDRESS);
      fail("missing mandatory value not detected: " + result);
    } catch (RuntimeException expected) {
      // expected
    }
  }

  //@@@ test other handler methods as well
}
