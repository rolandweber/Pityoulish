/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.msgboard.MixedMessageBoardImpl; //@@@ use JMockit instead
import pityoulish.tickets.DefaultTicketManager; //@@@ use JMockit instead
import pityoulish.sockets.server.MsgBoardRequest.ReqType;
import pityoulish.sockets.tlv.MsgBoardType;

import org.junit.*;
import static org.junit.Assert.*;


public class RequestHandlerImplTest
{
  @Test public void handle_LM_error()
  {
    //@@@ use JMockit for prereq objects, instead of creating default impls
    RequestHandler handler = new RequestHandlerImpl
      (new TLVRequestParserImpl(),
       new MsgBoardRequestHandlerImpl(new MixedMessageBoardImpl(8),
                                      new DefaultTicketManager()),
       new TLVResponseBuilderImpl()
       );
    byte[] reqpdu = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      (byte) 0x82, (byte) 0, (byte) 0
      // missing mandatory LIMIT
    };

    byte[] rsppdu = handler.handle(reqpdu, 0, reqpdu.length);

    assertNotNull("no response PDU");
    assertEquals("unexpected response type",
                 MsgBoardType.ERROR_RESPONSE.typeByte, rsppdu[0]);
    //@@@ check for contained text, occurrence of LIMIT?
  }


  /* valid request PDU:
    byte[] reqpdu = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      (byte) 0x82, (byte) 0, (byte) 5,

      MsgBoardType.LIMIT.typeByte,
      (byte) 0x82, (byte) 0, (byte) 1,
      (byte) 17
    };
   */

}
