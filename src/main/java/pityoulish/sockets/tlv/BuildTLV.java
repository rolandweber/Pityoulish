/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;

import java.io.UnsupportedEncodingException;

import pityoulish.sockets.tlv.ProtocolConstants.TLVType;


/**
 * Represents a TLV structure to be constructed.
 */
public class BuildTLV extends AnyTLV
{
  /**
   * Creates a new TLV to be stored in the given array.
   * The type and a length of 0 are written to the data immediately.
   * The length can be updated later.
   *
   * @param ty          the type for the new TLV structure
   * @param data        the byte array in which to store the structure
   * @param pos         index of the type byte in the array
   */
  public BuildTLV(TLVType ty, byte[] data, int pos)
  {
    super(data, pos);
    if (ty == null)
       throw new NullPointerException("TLVType");

    tlvType = ty;

    data[pos]   = ty.getTypeByte();
    data[pos+1] = ProtocolConstants.LENGTH_OF_LENGTH_2;
    setLength(0);
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

    valLength = len;
    tlvData[tlvStart+2] = hi;
    tlvData[tlvStart+3] = lo;
  }


  /**
   * Changes the length of the value.
   * The argument delta is added to the cached length.
   * This modifies the {@link #getData data} array
   * and updates the cached length.
   *
   * @param delta       the change of length, typically positive
   *                    because data is added to the value.
   *                    The resulting length must be between 0 and 65535.
   */
  public final void addToLength(int delta)
  {
    setLength(valLength+delta);
  }


  /**
   * Sets the value to a string and updates the length.
   * This is expected to be used only with encodings that are guaranteed
   * to be available. Therefore, an {@link UnsupportedEncodingException}
   * is mapped to a {@link RuntimeException}.
   * The underlying array needs to be large enough to hold the value.
   * Otherwise, an exception is thrown, but the array is partially modified.
   *
   * @param text        the string to set the value to
   * @param enc         the encoding to use, typically "UTF-8" or "US-ASCII"
   */
  public void setTextValue(String text, String enc)
  {
    try {
      byte[] tb = text.getBytes(enc);
      System.arraycopy(tb, 0, tlvData, getValueStart(), tb.length);
      setLength(tb.length);
    } catch (UnsupportedEncodingException uex) {
      throw new RuntimeException(uex);
    }
  }


}
