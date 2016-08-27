/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.nio.ByteBuffer;

import pityoulish.sockets.server.MsgBoardRequest.ReqType;
import pityoulish.sockets.tlv.MsgBoardTLV;
import pityoulish.sockets.tlv.MsgBoardType;

import org.junit.*;
import static org.junit.Assert.*;


public class TLVRequestBuilderImplTest
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



  @Test public void buildListMessages_LM()
  {
    RequestBuilder rb = new TLVRequestBuilderImpl();
    int    limit  = 8;
    String marker = "here";

    ByteBuffer buffer = rb.buildListMessages(limit, marker);
    byte[] pdu = toBytes(buffer);

    // The protocol allows for the arguments to occur in any order.
    // This test case is overspecified, because it assumes a fixed order.
    byte[] expected = new byte[]{
      MsgBoardType.LIST_MESSAGES.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x0d,

      MsgBoardType.LIMIT.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x01,
      (byte)limit,

      MsgBoardType.MARKER.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x04,
      (byte)'h', (byte)'e', (byte)'r', (byte)'e'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }


  @Test public void buildListMessages_L()
  {
    RequestBuilder rb = new TLVRequestBuilderImpl();
    int    limit  = 17;
    String marker = null;

    ByteBuffer buffer = rb.buildListMessages(limit, marker);
    byte[] pdu = toBytes(buffer);

    // The protocol allows for the arguments to occur in any order.
    // This test case is overspecified, because it assumes a fixed order.
    byte[] expected = new byte[]{
      MsgBoardType.LIST_MESSAGES.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x05,

      MsgBoardType.LIMIT.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x01,
      (byte)limit
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }


  @Test public void buildPutMessage_ASCII()
  {
    RequestBuilder rb = new TLVRequestBuilderImpl();
    String ticket = "pass";
    String text   = "Here I am.";

    ByteBuffer buffer = rb.buildPutMessage(ticket, text);
    byte[] pdu = toBytes(buffer);

    // The protocol allows for the arguments to occur in any order.
    // This test case is overspecified, because it assumes a fixed order.
    byte[] expected = new byte[]{
      MsgBoardType.PUT_MESSAGE.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)(8 + ticket.length() + text.length()),

      // TLV order is different from method argument order.
      MsgBoardType.TEXT.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)text.length(),
      (byte)'H', (byte)'e', (byte)'r', (byte)'e', (byte)' ',
      (byte)'I', (byte)' ', (byte)'a', (byte)'m', (byte)'.', 

      MsgBoardType.TICKET.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)ticket.length(),
      (byte)'p', (byte)'a', (byte)'s', (byte)'s'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }


  @Test public void buildPutMessage_UTF8() // non-ASCII char in text
  {
    RequestBuilder rb = new TLVRequestBuilderImpl();
    String ticket = "Free!";
    String text   = "\u00a4hem"; // A-umlaut

    ByteBuffer buffer = rb.buildPutMessage(ticket, text);
    byte[] pdu = toBytes(buffer);

    // The protocol allows for the arguments to occur in any order.
    // This test case is overspecified, because it assumes a fixed order.
    byte[] expected = new byte[]{
      MsgBoardType.PUT_MESSAGE.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)(8 + ticket.length() + text.length()+1),

      // TLV order is different from method argument order.
      MsgBoardType.TEXT.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)(text.length()+1),
      (byte)0xc2, (byte)0xa4, (byte)'h', (byte)'e', (byte)'m',

      MsgBoardType.TICKET.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)ticket.length(),
      (byte)'F', (byte)'r', (byte)'e', (byte)'e', (byte)'!'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }


  @Test public void buildObtainTicket()
  {
    RequestBuilder rb = new TLVRequestBuilderImpl();
    String username = "me";

    ByteBuffer buffer = rb.buildObtainTicket(username);
    byte[] pdu = toBytes(buffer);

    byte[] expected = new byte[]{
      MsgBoardType.OBTAIN_TICKET.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)(4 + username.length()),

      MsgBoardType.ORIGINATOR.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)username.length(),
      (byte)'m', (byte)'e'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }


  @Test public void buildReturnTicket()
  {
    RequestBuilder rb = new TLVRequestBuilderImpl();
    String ticket = "eNtEr";

    ByteBuffer buffer = rb.buildReturnTicket(ticket);
    byte[] pdu = toBytes(buffer);

    byte[] expected = new byte[]{
      MsgBoardType.RETURN_TICKET.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)(4 + ticket.length()),

      MsgBoardType.TICKET.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)ticket.length(),
      (byte)'e', (byte)'N', (byte)'t', (byte)'E', (byte)'r'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }


  @Test public void buildReplaceTicket()
  {
    RequestBuilder rb = new TLVRequestBuilderImpl();
    String ticket = "-X-";

    ByteBuffer buffer = rb.buildReplaceTicket(ticket);
    byte[] pdu = toBytes(buffer);

    byte[] expected = new byte[]{
      MsgBoardType.REPLACE_TICKET.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)(4 + ticket.length()),

      MsgBoardType.TICKET.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)ticket.length(),
      (byte)'-', (byte)'X', (byte)'-'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }

}
