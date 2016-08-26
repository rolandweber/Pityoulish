/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;

import java.nio.ByteBuffer;

import org.junit.*;
import static org.junit.Assert.*;


public class AbstractTLVTest
{
  /** Concrete subclass to test with. */
  public static class JUnitTLV extends AbstractTLV<Byte>
  {
    public JUnitTLV(byte[] data, int pos)
    {
      super(data, pos);
      update();
    }

    protected Byte determineType()
    {
      return tlvData[tlvStart];
    }

    protected int determineLength()
    {
      return tlvData[tlvStart+1];
    }

    protected int getValueOffset()
    {
      // 1 byte for the type
      // 1 byte for the length
      return 2;
    }

  } // class JUnitTLV



  @Test public void construct_plain()
  {
    final byte TAG = (byte) 0xC0;
    byte[] data = new byte[] {
      TAG, (byte) 3, (byte) 'a', (byte) 'b', (byte) 'c'
    };

    JUnitTLV tlv = new JUnitTLV(data, 0);
    assertNotNull("no TLV", tlv);
    assertSame("wrong data", data, tlv.getData());

    assertEquals("wrong size", 5, tlv.getSize());
    assertEquals("wrong type", new Byte(TAG), tlv.getType());
    assertEquals("wrong length", 3, tlv.getLength());

    assertEquals("wrong start", 0, tlv.getStart());
    assertEquals("wrong start of value", 2, tlv.getValueStart());
    assertEquals("wrong end", 5, tlv.getEnd());
  }


  @Test public void construct_within()
  {
    final byte TAG = (byte) 0xC1;
    byte[] data = new byte[] {
      (byte) 0x88, (byte) 0x88, // trash data
      TAG, (byte) 3, (byte) 'd', (byte) 'e', (byte) 'f',
      (byte) 0x88 // more trash data
    };

    JUnitTLV tlv = new JUnitTLV(data, 2);
    assertNotNull("no TLV", tlv);
    assertSame("wrong data", data, tlv.getData());

    assertEquals("wrong size", 5, tlv.getSize());
    assertEquals("wrong type", new Byte(TAG), tlv.getType());
    assertEquals("wrong length", 3, tlv.getLength());

    assertEquals("wrong start", 2, tlv.getStart());
    assertEquals("wrong start of value", 4, tlv.getValueStart());
    assertEquals("wrong end", 7, tlv.getEnd());
  }


  @Test public void construct_invalid()
  {
    final byte TAG = (byte) 0xBD;
    byte[] data = new byte[] {
      TAG, (byte) 3, (byte) 'a', (byte) 'b', (byte) 'c'
    };

    try {
      JUnitTLV tlv = new JUnitTLV(null, 0);
      fail("missing data not detected");
    } catch (RuntimeException expected) {
      // ok
    }


    try {
      JUnitTLV tlv = new JUnitTLV(data, -1);
      fail("negative position not detected");
    } catch (RuntimeException expected) {
      // ok
    }


    try {
      JUnitTLV tlv = new JUnitTLV(data, data.length);
      fail("position beyond data not detected");
    } catch (RuntimeException expected) {
      // ok
    }
  }


  @Test public void update()
  {
    final byte TAG1 = (byte) 0xC2;
    final byte TAG2 = (byte) 0xCD;
    byte[] data = new byte[] {
      TAG1, (byte) 4,
      (byte) 'l', (byte) 'o', (byte) 'n', (byte) 'g', (byte) 'e', (byte) 'r'
    };

    JUnitTLV tlv = new JUnitTLV(data, 0);
    assertEquals("wrong size before update", 6, tlv.getSize());
    assertEquals("wrong type before update", new Byte(TAG1), tlv.getType());
    assertEquals("wrong length before update", 4, tlv.getLength());
    assertEquals("wrong end before update", 6, tlv.getEnd());

    data[0] = TAG2;     // change the type
    data[1] = (byte) 6; // change the length
    tlv.update();

    assertEquals("wrong size after update", 8, tlv.getSize());
    assertEquals("wrong type after update", new Byte(TAG2), tlv.getType());
    assertEquals("wrong length after update", 6, tlv.getLength());
    assertEquals("wrong end after update", 8, tlv.getEnd());
  }


  @Test public void copyValue()
  {
    final byte TAG = (byte) 0xC3;
    byte[] data = new byte[] {
      (byte) 0x88, // trash data
      TAG, (byte) 3, (byte) 'g', (byte) 'h', (byte) 'i',
      (byte) 0x88, (byte) 0x88 // more trash data
    };

    JUnitTLV tlv = new JUnitTLV(data, 1);
    byte[] value = tlv.copyValue();

    assertNotNull("no value", value);
    assertArrayEquals("wrong value",
                      new byte[]{ (byte) 'g', (byte) 'h', (byte) 'i' },
                      value);
  }


  @Test public void copyTLV()
  {
    final byte TAG = (byte) 0xC3;
    byte[] data = new byte[] {
      (byte) 0x88, // trash data
      TAG, (byte) 3, (byte) 'g', (byte) 'h', (byte) 'i',
      (byte) 0x88, (byte) 0x88 // more trash data
    };

    JUnitTLV tlv = new JUnitTLV(data, 1);
    byte[] tlvdata = tlv.copyTLV();

    assertNotNull("no data", tlvdata);
    assertArrayEquals("wrong data",
                      new byte[]{ TAG, (byte) 3,
                                  (byte) 'g', (byte) 'h', (byte) 'i' },
                      tlvdata);
  }


  @Test public void toBuffer()
  {
    final byte TAG = (byte) 0xC3;
    byte[] data = new byte[] {
      (byte) 0x88, // trash data
      TAG, (byte) 3, (byte) 'g', (byte) 'h', (byte) 'i',
      (byte) 0x88, (byte) 0x88 // more trash data
    };

    JUnitTLV   tlv = new JUnitTLV(data, 1);
    ByteBuffer buf = tlv.toBuffer();
    assertNotNull("no buffer", buf);

    assertEquals("wrong data size", 5, buf.remaining());
    assertSame("wrong backing array", data, buf.array());

    byte[] tlvdata = new byte[buf.remaining()];
    buf.get(tlvdata);

    assertArrayEquals("wrong value",
                      new byte[]{ TAG, (byte) 3,
                                  (byte) 'g', (byte) 'h', (byte) 'i' },
                      tlvdata);
  }

}
