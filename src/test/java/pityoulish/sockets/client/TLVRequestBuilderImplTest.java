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

}
