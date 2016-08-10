/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;

import pityoulish.sockets.tlv.ProtocolConstants.TLVType;


/**
 * Represents a TLV structure referring to a byte array.
 * The TLV does not necessarily begin at the first byte in the array.
 * Use derived classes to interpret existing TLVs, or to build new ones.
 * This class supports only the restricted BER format of the example protocol.
 */
public class AnyTLV
{
  protected byte[] tlvData;

  protected int tlvStart;

  protected TLVType tlvType;

  protected int valLength;


  /**
   * Creates a new TLV pointing to the provided data.
   * This does <i>not</i> initialize the cached type and length,
   * call {@link #update} if necessary.
   *
   * @param data        the byte array in which the structure is stored
   * @param pos         index of the type byte in the array
   */
  protected AnyTLV(byte[] data, int pos)
  {
    if (data == null)
       throw new NullPointerException("data");
    if ((pos < 0) || (pos >= data.length))
       throw new IllegalArgumentException
         ("pos "+pos+" out of range "+data.length);

    tlvData = data;
    tlvStart = pos;
  }


  /**
   * Updates the cached type and length from the underlying data.
   * Typically called by a constructor, if needed.
   * Can also be called explicitly when the underlying data has changed.
   */
  public void update()
  {
    final byte tb = getTypeByte(); // type byte
    TLVType    te = null;          // type enum

    for (TLVType candidate : TLVType.values())
     {
       if (candidate.getTypeByte() == tb)
        {
          te = candidate;
          break;
        }
     }
    tlvType = te; // might still be null

    // only the encoding with length-of-length 2 is supported
    if (tlvData[tlvStart+1] != ProtocolConstants.LENGTH_OF_LENGTH_2)
       throw new IllegalStateException("unsupported length encoding");

    int hi = tlvData[tlvStart+2] & 0xff;
    int lo = tlvData[tlvStart+3] & 0xff;

    valLength = (hi << 8) + lo;

  } // update


  /** Obtains the <i>type</i> byte. */
  public final byte getTypeByte()
  {
    return tlvData[tlvStart];
  }


  /**
   * Obtains the <i>type</i> enum constant.
   * This is a cached type, call {@link #update} to refresh.
   *
   * @return    the <i>type</i> constant, or
   *            <code>null</code> if none of the defined constants
   *            match the type byte
   */
  public final TLVType getType()
  {
    return tlvType;
  }


  /**
   * Obtains the size of the TLV structure, in bytes.
   * This relies on a cached length, call {@link #update} to refresh.
   *
   * @return    the size of this TLV, in bytes.
   *            This is the overall size, including type and length bytes.
   */
  public final int getSize()
  {
    return 4+valLength;
  }


  /**
   * Obtains the <i>length</i> of the value of the TLV structure, in bytes.
   * This is a cached length, call {@link #update} to refresh.
   *
   * @return    the <i>length</i> of the value of this TLV, in bytes.
   *            This is a net length, excluding type and length bytes.
   */
  public final int getLength()
  {
    return valLength;
  }


  /** Obtains the underlying data array. */
  public final byte[] getData()
  {
    return tlvData;
  }


  /** Obtains the index of the first byte of this TLV. */
  public final int getStart()
  {
    return tlvStart;
  }


  /** Obtains the index of the first byte of the value. */
  public final int getValueStart()
  {
    return tlvStart+4;
  }


  /**
   * Obtains the index after the last byte of this TLV.
   * This relies on the cached length, call {@link #update} to refresh.
   */
  public final int getEnd()
  {
    return getValueStart() + valLength;
  }


  /**
   * Copies the value of this TLV into a new byte array.
   * This relies on the cached length, call {@link #update} to refresh.
   */
  public final byte[] copyValue()
  {
    byte[] result = new byte[valLength];
    System.arraycopy(tlvData, getValueStart(), result, 0, valLength);
    return result;
  }


  /**
   * Generates an extended string representation of this TLV, including data.
   * This relies on the cached length, call {@link #update} to refresh.
   */
  public final String toFullString()
  {
    StringBuilder sb = new StringBuilder(30+3*valLength);

    sb.append("TLV(");
    if (tlvType != null)
       sb.append(tlvType);
    else
       appendHex(sb, tlvData[tlvStart]);
    sb.append(':').append(valLength);

    int end = getEnd();
    if (end > tlvData.length)
     {
       end = tlvData.length;
       sb.append('?');
     }
    sb.append(':');

    for (int pos=getValueStart(); pos < end; pos++)
     {
       sb.append(' ');
       appendHex(sb, tlvData[pos]);
     }

    sb.append(')');

    return sb.toString();
  }


  public final static String HEX = "0123456789abcedf";
  /**
   * Appends the hex dump of a byte to a string builder.
   * This has nothing to do with TLVs.
   */
  public final static void appendHex(StringBuilder sb, byte b)
  {
    sb.append(HEX.charAt( (b>>4) & 0x0f));
    sb.append(HEX.charAt(  b     & 0x0f));
  }

}
