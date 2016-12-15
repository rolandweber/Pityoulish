/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import pityoulish.msgboard.MixedMessageBoardImpl; //@@@ use JMockit instead
import pityoulish.tickets.DefaultTicketManager; //@@@ use JMockit instead
import pityoulish.sockets.server.MsgBoardRequest.ReqType;
import pityoulish.sockets.tlv.MsgBoardType;

import org.junit.*;
import static org.junit.Assert.*;


public class RequestHandlerImplTest
{
  private final static InetAddress ADDRESS;
  static {
    try {
      ADDRESS = InetAddress.getLocalHost();
    } catch (Exception x) {
      throw new ExceptionInInitializerError(x);
    }
  }


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

    ByteBuffer rspbuf = handler.handle(ByteBuffer.wrap(reqpdu), ADDRESS);

    assertNotNull("no response buffer");
    assertEquals("unexpected response type",
                 MsgBoardType.ERROR_RESPONSE.typeByte, rspbuf.get());
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
