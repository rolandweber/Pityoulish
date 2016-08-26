/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.nio.ByteBuffer;

import pityoulish.sockets.server.MsgBoardRequest.ReqType;
import pityoulish.sockets.tlv.MsgBoardTLV;
import pityoulish.sockets.tlv.MsgBoardType;

import org.junit.*;
import static org.junit.Assert.*;


public class TLVResponseBuilderImplTest
{
  /**
   * Obtains the data from a ByteBuffer as an array.
   * This modifies the position of the buffer.
   *
   * @param buffer   the buffer to read from
   *
   * @return the data read from the buffer
   */
  public static byte[] toBytes(ByteBuffer buffer)
  {
    assertNotNull("no ByteBuffer", buffer);

    int length = buffer.remaining();
    byte[] data = new byte[length];
    buffer.get(data);

    return data;
  }



  @Test public void buildInfoResponse_text()
  {
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    String msg = "H\u00e4ppy!"; // a-umlaut

    ByteBuffer buf = rb.buildInfoResponse(msg);
    byte[]     pdu = toBytes(buf);

    byte[] expected = new byte[]{
      MsgBoardType.INFO_RESPONSE.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x0b,

      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x07,
      (byte)'H', (byte)0xc3, (byte)0xa4,
      (byte)'p', (byte)'p', (byte)'y', (byte)'!'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }


  @Test public void buildErrorResponse_text()
  {
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    String msg = "b\u00e4d"; // a-umlaut

    ByteBuffer buf = rb.buildErrorResponse(msg);
    byte[]     pdu = toBytes(buf);

    byte[] expected = new byte[]{
      MsgBoardType.ERROR_RESPONSE.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x08,

      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x04,
      (byte)'b', (byte)0xc3, (byte)0xa4, (byte)'d'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }


  @Test public void buildErrorResponse_exception()
    throws Exception
  {
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    String why = "zero";
    Exception cause = new NullPointerException(why);

    ByteBuffer buf = rb.buildErrorResponse(cause);
    byte[]     pdu = toBytes(buf);

    // Cannot check for exact PDU without making assumptions about
    // the implementation of buildErrorResponse(Throwable). Instead:
    // - check for TLV nesting, ignore lengths
    // - check for presence of classname and exception message in data

    assertEquals("wrong response TLV type",
                 MsgBoardType.ERROR_RESPONSE.getTypeByte(), pdu[0]);
    assertEquals("wrong content TLV type",
                 MsgBoardType.TEXT.getTypeByte(), pdu[4]);
    String text = new String(pdu, 8, pdu.length-8, "UTF-8");
    assertTrue("missing exception class in output",
               text.indexOf(cause.getClass().getName()) >= 0);
    assertTrue("missing exception message in output",
               text.indexOf(why) >= 0);
  }


  //@@@ @Test public void buildMessageBatch()
  //@@@ different numbers of messages: 0, 1, several
  //@@@ with and without discontinuity warning


  @Test public void buildTicketGrant()
  {
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    String tictok = "tIckEt35";

    ByteBuffer buf = rb.buildTicketGrant(tictok);
    byte[]     pdu = toBytes(buf);

    byte[] expected = new byte[]{
      MsgBoardType.TICKET_GRANT.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x0c,

      MsgBoardType.TICKET.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x08,
      (byte)'t', (byte)'I', (byte)'c', (byte)'k',
      (byte)'E', (byte)'t', (byte)'3', (byte)'5'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }

}
