/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import pityoulish.msgboard.Message;
import pityoulish.msgboard.MessageImpl;
import pityoulish.msgboard.MessageBatch;
import pityoulish.msgboard.MessageBatchImpl;

import pityoulish.sockets.server.MsgBoardRequest.ReqType;
import pityoulish.sockets.tlv.MsgBoardTLV;
import pityoulish.sockets.tlv.MsgBoardType;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


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


  /**
   * Counts how often a byte occurs in an array.
   * Since Hamcrest matchers don't work well on byte arrays,
   * let's use this workaround.
   *
   * @param data        the array to consider
   * @param value       the element to look for
   *
   * @return    number of times the value occurs in the data
   */
  public static int count(byte[] data, byte value)
  {
    if (data == null)
       return 0;

    int c = 0;
    for (byte b: data)
       if (b == value)
          c++;

    return c;
  }


  /**
   * Asserts a count for a TLV type.
   *
   * @param mbt    the TLV type to count
   * @param pdu    the array in which to count
   * @param expected   the expected number of occurrences
   */
  public static void assertCount(MsgBoardType mbt, byte[] pdu, int expected)
  {
    assertEquals("wrong count for "+mbt,
                 expected, count(pdu, mbt.getTypeByte()));
  }



  @Test public void buildInfoResponse_good()
  {
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    String msg = "H\u00e4ppy!"; // a-umlaut
    MsgBoardResponse<String> rsp = new MsgBoardResponseImpl.Info(msg);

    ByteBuffer buf = rb.buildInfoResponse(rsp);
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


  @Test public void buildInfoResponse_bad()
  {
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    String msg = "b\u00e4d"; // a-umlaut
    MsgBoardResponse<String> rsp = new MsgBoardResponseImpl.Error(msg);

    ByteBuffer buf = rb.buildInfoResponse(rsp);
    byte[]     pdu = toBytes(buf);

    byte[] expected = new byte[]{
      MsgBoardType.ERROR_RESPONSE.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x08,

      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x04,
      (byte)'b', (byte)0xc3, (byte)0xa4, (byte)'d'
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


  @Test public void buildMessageBatch_one()
  {
    // one message, discontinuous... means each TLV appears exactly once
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    List<? extends Message> msgs =
      Arrays.asList(new MessageImpl("me", "now", "silence"));
    String   marker = "pin";
    MessageBatch mb = new MessageBatchImpl(msgs, marker, true);

    ByteBuffer buf = rb.buildMessageBatch(mb);
    byte[]     pdu = toBytes(buf);

    // the order of immediately nested TLVs is specified
    // offsets are easy enough to compute for direct verification
    assertEquals("wrong response TLV type",
                 MsgBoardType.MESSAGE_BATCH.getTypeByte(), pdu[0]);
    assertEquals("wrong type of first nested TLV",
                 MsgBoardType.MARKER.getTypeByte(), pdu[4]);
    assertEquals("wrong type of second nested TLV",
                 MsgBoardType.MISSED.getTypeByte(), pdu[8+marker.length()]);
    assertEquals("wrong type of third nested TLV",
                 MsgBoardType.MESSAGE.getTypeByte(), pdu[12+marker.length()]);

    // The order of nested TLVs within a MESSAGE is random.
    // I'd like to use Hamcrest matchers here, but they don't work well
    // for arrays of a primitive type. http://stackoverflow.com/q/18366109
    // Luckily, our TLV types don't clash with ASCII characters.

    assertCount(MsgBoardType.MESSAGE_BATCH, pdu, 1);
    assertCount(MsgBoardType.MARKER, pdu, 1);
    assertCount(MsgBoardType.MISSED, pdu, 1);
    assertCount(MsgBoardType.MESSAGE, pdu, 1);
    assertCount(MsgBoardType.ORIGINATOR, pdu, 1);
    assertCount(MsgBoardType.TIMESTAMP, pdu, 1);
    assertCount(MsgBoardType.TEXT, pdu, 1);
  }


  @Test public void buildMessageBatch_none()
  {
    // no message, continuous
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    List<? extends Message> msgs = Arrays.<Message> asList();
    String   marker = "here";
    MessageBatch mb = new MessageBatchImpl(msgs, marker, false);

    ByteBuffer buf = rb.buildMessageBatch(mb);
    byte[]     pdu = toBytes(buf);

    // the order of immediately nested TLVs is specified
    // offsets are easy enough to compute for direct verification
    assertEquals("wrong response TLV type",
                 MsgBoardType.MESSAGE_BATCH.getTypeByte(), pdu[0]);
    assertEquals("wrong type of first nested TLV",
                 MsgBoardType.MARKER.getTypeByte(), pdu[4]);

    assertCount(MsgBoardType.MESSAGE_BATCH, pdu, 1);
    assertCount(MsgBoardType.MARKER, pdu, 1);
    assertCount(MsgBoardType.MISSED, pdu, 0);
    assertCount(MsgBoardType.MESSAGE, pdu, 0);
    assertCount(MsgBoardType.ORIGINATOR, pdu, 0);
    assertCount(MsgBoardType.TIMESTAMP, pdu, 0);
    assertCount(MsgBoardType.TEXT, pdu, 0);
  }


  @Test public void buildMessageBatch_some()
  {
    // several message, continuous
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    List<? extends Message> msgs =
      Arrays.asList(new MessageImpl("me", "now", "shout"),
                    new MessageImpl("you", "then", "scream"),
                    new MessageImpl("they", "never", "speak")
                    );
    String   marker = "arrow";
    MessageBatch mb = new MessageBatchImpl(msgs, marker, false);

    ByteBuffer buf = rb.buildMessageBatch(mb);
    byte[]     pdu = toBytes(buf);

    // the order of immediately nested TLVs is specified
    // offsets are easy enough to compute for direct verification
    assertEquals("wrong response TLV type",
                 MsgBoardType.MESSAGE_BATCH.getTypeByte(), pdu[0]);
    assertEquals("wrong type of first nested TLV",
                 MsgBoardType.MARKER.getTypeByte(), pdu[4]);
    assertEquals("wrong type of second nested TLV",
                 MsgBoardType.MESSAGE.getTypeByte(), pdu[8+marker.length()]);

    // The order of nested TLVs within a MESSAGE is random.
    // I'd like to use Hamcrest matchers here, but they don't work well
    // for arrays of a primitive type. http://stackoverflow.com/q/18366109
    // Luckily, our TLV types don't clash with ASCII characters.

    assertCount(MsgBoardType.MESSAGE_BATCH, pdu, 1);
    assertCount(MsgBoardType.MARKER, pdu, 1);
    assertCount(MsgBoardType.MISSED, pdu, 0);
    assertCount(MsgBoardType.MESSAGE,    pdu, msgs.size());
    assertCount(MsgBoardType.ORIGINATOR, pdu, msgs.size());
    assertCount(MsgBoardType.TIMESTAMP,  pdu, msgs.size());
    assertCount(MsgBoardType.TEXT,       pdu, msgs.size());
  }


  @Test public void buildTicketGrant_good()
  {
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    String tictok = "tIckEt35";
    MsgBoardResponse<String> rsp = new MsgBoardResponseImpl.Ticket(tictok);

    ByteBuffer buf = rb.buildTicketGrant(rsp);
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


  @Test public void buildTicketGrant_bad()
  {
    ResponseBuilder rb = new TLVResponseBuilderImpl();
    String msg = "b\u00e4d"; // a-umlaut
    MsgBoardResponse<String> rsp = new MsgBoardResponseImpl.Error(msg);

    ByteBuffer buf = rb.buildTicketGrant(rsp);
    byte[]     pdu = toBytes(buf);

    byte[] expected = new byte[]{
      MsgBoardType.ERROR_RESPONSE.getTypeByte(),
      (byte)0x82, (byte)0x00, (byte)0x08,

      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x04,
      (byte)'b', (byte)0xc3, (byte)0xa4, (byte)'d'
    };
    assertArrayEquals("wrong PDU", expected, pdu);
  }

}
