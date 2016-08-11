/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;

import java.io.UnsupportedEncodingException;


/**
 * Represents a TLV of the binary protocol for Message Boards.
 * This class supports only the restricted BER format of the binary protocol.
 * In particular, the type is encoded as a single byte
 * and the length with exactly three bytes.
 */
public class MsgBoardTLV extends AbstractTLV<MsgBoardType>
  implements ParseTLV<MsgBoardType>, BuildTLV<MsgBoardType>
{
  /**
   * A <i>length of length</i> value indicating 2 bytes for the length.
   * After the tag, the length of the TLV structure must be specified.
   * The protocol uses Basic Encoding Rules (BER) with this length-of-length
   * byte, followed by two bytes that hold the actual length.
   * The higher-order byte is stored before the lower-order byte.
   */
  public final static byte LENGTH_OF_LENGTH_2 = (byte)0x82;


  /**
   * Creates a new TLV for parsing the provided data.
   * This initializes the cached type and length.
   *
   * @param data        the byte array in which the structure is stored
   * @param pos         index of the type byte in the array
   */
  public MsgBoardTLV(byte[] data, int pos)
  {
    super(data, pos);
    update();
  }


  /**
   * Creates a new TLV to be stored in the given array.
   * The type and a length of 0 are written to the data immediately.
   * The length can be updated later.
   *
   * @param mbt         the type for the new TLV structure
   * @param data        the byte array in which to store the structure
   * @param pos         index of the type byte in the array
   */
  public MsgBoardTLV(MsgBoardType mbt, byte[] data, int pos)
  {
    super(data, pos);
    if (mbt == null)
       throw new NullPointerException("MsgBoardType");

    tlvType = mbt;

    data[pos]   = mbt.getTypeByte();
    data[pos+1] = LENGTH_OF_LENGTH_2;
    setLength(0);
  }


  // non-javadoc, see base class
  protected MsgBoardType determineType()
  {
    final byte   tb = tlvData[tlvStart]; // type byte
    MsgBoardType te = null;              // type enum

    // a map for reverse lookup would provide better performance
    // as would a switch statement, but that duplicates info from the enum
    for (MsgBoardType candidate : MsgBoardType.values())
     {
       if (candidate.getTypeByte() == tb)
        {
          te = candidate;
          break;
        }
     }

    if (te == null)
       throw new IllegalStateException("unknown type "+tb);

    return te;
  }


  // non-javadoc, see base class
  protected int determineLength()
  {
    // only single-byte types are supported
    // only the encoding with length-of-length 2 is supported
    if (tlvData[tlvStart+1] != LENGTH_OF_LENGTH_2)
       throw new IllegalStateException("unsupported length encoding");

    int hi = tlvData[tlvStart+2] & 0xff;
    int lo = tlvData[tlvStart+3] & 0xff;

    int length = (hi << 8) + lo;

    return length;
  }


  // non-javadoc, see base class
  protected int getValueOffset()
  {
    // 1 byte for the type
    // 3 byte for the length
    return 4;
  }


  /**
   * Updates the length of the value.
   * This modifies the {@link #getData data} array
   * and updates the cached length.
   *
   * @param len         the new length, between 0 and 65535
   */
  public void setLength(int len)
  {
    if ((len < 0) || (len > 0xffff))
       throw new IllegalArgumentException("invalid length "+len);

    byte lo = (byte) ( len       & 0xff);
    byte hi = (byte) ((len >> 8) & 0xff);

    valueLength = len;
    tlvData[tlvStart+2] = hi;
    tlvData[tlvStart+3] = lo;
  }


  // non-javadoc, see interface BuildTLV
  public final void addToLength(int delta)
  {
    setLength(valueLength+delta);
  }


  // non-javadoc, see interface BuildTLV
  public void setTextValue(String text, String enc)
  {
    if (!tlvType.isPrimitive())
       throw new IllegalStateException(String.valueOf(tlvType));

    try {
      byte[] tb = text.getBytes(enc);
      System.arraycopy(tb, 0, tlvData, getValueStart(), tb.length);
      setLength(tb.length);
    } catch (UnsupportedEncodingException uex) {
      throw new RuntimeException(uex);
    }
  }


  // non-javadoc, see interface BuildTLV
  public MsgBoardTLV appendTLV(MsgBoardType mbt)
  {
    return new MsgBoardTLV(mbt, tlvData, this.getEnd());
  }

}
