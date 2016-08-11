/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;

import org.junit.*;
import static org.junit.Assert.*;

public class MsgBoardTLVTest
{
  /** MUST be treated read-only! */
  private static byte[] SAMPLE_DATA = new byte[]{
    // the constructed example TLV from the Binary Protocol spec
    // TLV 1, constructed
    (byte)0xE5, (byte)0x82, (byte)0x00, (byte)0x12,
    // nested TLV 1.1
    (byte)0xC6, (byte)0x82, (byte)0x00, (byte)0x04,
    (byte)0x66, (byte)0x72, (byte)0x65, (byte)0x65,
    // nested TLV 1.2
    (byte)0xC0, (byte)0x82, (byte)0x00, (byte)0x06,
    (byte)0x48, (byte)0x65, (byte)0x6C, (byte)0x6C, (byte)0x6F, (byte)0x21,
    // end of TLV 1
  };


  @Test public void construct_OK()
  {
    MsgBoardTLV tlv = new MsgBoardTLV(SAMPLE_DATA, 0);
    assertNotNull("no TLV", tlv);
    assertEquals("wrong size", 22, tlv.getSize());
    assertEquals("wrong type", MsgBoardType.PUT_MESSAGE, tlv.getType());
  }


  @Test public void construct_BadType()
  {
    byte[] data = new byte[]{
      (byte)0xFF, (byte)0x82, (byte)0x00, (byte)0x01, (byte)0x00
    };

    try {
      MsgBoardTLV tlv = new MsgBoardTLV(data, 0);
      fail("invalid type not detected");
    } catch (RuntimeException expected) {
      // ok
    }
  }


  @Test public void construct_BadLength()
  {
    // correct according to BER, but not allowed by the Binary Protocol
    byte[] data = new byte[]{
      (byte)0xFF, (byte)0x81, (byte)0x01, (byte)0x00
    };

    try {
      MsgBoardTLV tlv = new MsgBoardTLV(data, 0);
      fail("invalid length not detected");
    } catch (RuntimeException expected) {
      // ok
    }
  }


  @Test public void setLength()
  {
    byte[] data = new byte[8];

    MsgBoardTLV tlv = new MsgBoardTLV(MsgBoardType.TEXT, data, 1);
    tlv.setLength(2);

    byte[] expected = new byte[]{
      (byte)0x00, // trash
      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x02,
      (byte)0x00, (byte)0x00, // fake content
      (byte)0x00 // trash
    };
    assertArrayEquals("unexpected data", expected, data);
  }


  @Test public void addToLength_positive()
  {
    // the length is a fake, the value wouldn't fit into the array
    byte[] data = new byte[]{
      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x7f
    };

    MsgBoardTLV tlv = new MsgBoardTLV(data, 0);
    tlv.addToLength(130);

    assertEquals("wrong length", 257, tlv.getLength());
    byte[] expected = new byte[]{
      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x01, (byte)0x01
    };
    assertArrayEquals("wrong data", expected, data);
  }


  @Test public void addToLength_negative()
  {
    byte[] data = new byte[]{
      (byte)0x00, // trash
      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x03,
      (byte)0x60, (byte)0x61, (byte)0x62
    };

    MsgBoardTLV tlv = new MsgBoardTLV(data, 1);
    tlv.addToLength(-2);

    assertEquals("wrong length", 1, tlv.getLength());
    byte[] expected = new byte[]{
      (byte)0x00, // trash
      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x01,
      (byte)0x60, (byte)0x61, (byte)0x62
    };
    assertArrayEquals("wrong data", expected, data);
  }


  @Test public void setTextValue_ASCII()
  {
    byte[] data = new byte[12];

    MsgBoardTLV tlv = new MsgBoardTLV(MsgBoardType.TEXT, data, 0);
    tlv.setTextValue("Hello", "US-ASCII");

    assertEquals("wrong length", 5, tlv.getLength());
    byte[] expected = new byte[]{
      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x05,
      (byte)0x48, (byte)0x65, (byte)0x6C, (byte)0x6C, (byte)0x6F,
      (byte)0x00, (byte)0x00, (byte)0x00 // trash
    };
    assertArrayEquals("unexpected data", expected, data);
  }


  @Test public void setTextValue_UTF8()
  {
    byte[] data = new byte[17];

    MsgBoardTLV tlv = new MsgBoardTLV(MsgBoardType.TEXT, data, 0);
    tlv.setTextValue("\u270cictory", "UTF-8");
    // U+270C is a hand showing the Victory (or Peace) sign

    System.out.println(tlv.toFullString());

    assertEquals("wrong length", 9, tlv.getLength());
    byte[] expected = new byte[]{
      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x09,
      (byte)0xE2, (byte)0x9C, (byte)0x8C, (byte)0x69, (byte)0x63,
      (byte)0x74, (byte)0x6F, (byte)0x72, (byte)0x79,
      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 // trash
    };
    assertArrayEquals("unexpected data", expected, data);
  }


  @Test public void appendTLV()
  {
    byte[] data = new byte[12];

    MsgBoardTLV first = new MsgBoardTLV(MsgBoardType.TEXT, data, 0);
    first.setLength(2);

    MsgBoardTLV next = first.appendTLV(MsgBoardType.ORIGINATOR);

    assertNotNull("no TLV", next);
    assertEquals("wrong start", 6, next.getStart());
    assertEquals("wrong size", 4, next.getSize());
    assertEquals("wrong type", MsgBoardType.ORIGINATOR, next.getType());

    byte[] expected = new byte[]{
      MsgBoardType.TEXT.getTypeByte(), (byte)0x82, (byte)0x00, (byte)0x02,
      (byte)0x00, (byte)0x00, // fake content
      MsgBoardType.ORIGINATOR.getTypeByte(),(byte)0x82, (byte)0x00, (byte)0x00,
      (byte)0x00, (byte)0x00 // trash
    };
    assertArrayEquals("unexpected data", expected, data);
  }



  @Test public void getNestedTLV_OK()
  {
    MsgBoardTLV constructed = new MsgBoardTLV(SAMPLE_DATA, 0);
    MsgBoardTLV nested = constructed.getNestedTLV();

    assertNotNull("no TLV", nested);
    assertEquals("wrong start", 4, nested.getStart());
    assertEquals("wrong size", 8, nested.getSize());
    assertEquals("wrong type", MsgBoardType.TICKET, nested.getType());
  }


  @Test public void getNestedTLV_none()
  {
    MsgBoardTLV primitive = new MsgBoardTLV(SAMPLE_DATA, 4);
    MsgBoardTLV nested = primitive.getNestedTLV();

    assertNull("unexpected nested TLV", nested);
  }


  @Test public void getNextTLV_OK()
  {
    MsgBoardTLV constructed = new MsgBoardTLV(SAMPLE_DATA, 0);
    MsgBoardTLV nested = constructed.getNestedTLV();
    MsgBoardTLV next   = nested.getNextTLV(constructed.getEnd());

    assertNotNull("no TLV", next);
    assertEquals("wrong start", 12, next.getStart());
    assertEquals("wrong size", 10, next.getSize());
    assertEquals("wrong type", MsgBoardType.TEXT, next.getType());
  }


  @Test public void getNextTLV_none()
  {
    MsgBoardTLV constructed = new MsgBoardTLV(SAMPLE_DATA, 0);
    MsgBoardTLV next = constructed.getNextTLV(constructed.getEnd());

    assertNull("unexpected next TLV", next);
  }


}

