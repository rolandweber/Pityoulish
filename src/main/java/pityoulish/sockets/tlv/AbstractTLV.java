/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;

import java.nio.ByteBuffer;


/**
 * Base class for implementing {@link TLV}.
 * This implementation caches type and length.
 */
public abstract class AbstractTLV<T> implements TLV<T>
{
  protected byte[] tlvData;

  protected int tlvStart;

  protected T tlvType;

  protected int valueLength;


  /**
   * Creates a new TLV pointing to the provided data.
   * This does <i>not</i> initialize the cached type and length,
   * call {@link #update} if necessary.
   *
   * @param data        the byte array in which the structure is stored
   * @param pos         index of the type byte in the array
   */
  protected AbstractTLV(byte[] data, int pos)
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
   * Derived classes should call this method when needed.
   * The default implementation delegates to
   * {@link #determineType} and {@link #determineLength}.
   *
   * @throws RuntimeException   if some of the data cannot be determined
   */
  // intentionally not final
  public void update()
    throws RuntimeException
  {
    // first store the values in local variables
    T   tlvT = determineType();
    int tlvL = determineLength();

    // there's no RuntimeException, so we can update both attributes now
    tlvType     = tlvT;
    valueLength = tlvL;
  }


  /**
   * Determines the <i>type</i> constant for this TLV.
   * Called by {@link #update}.
   *
   * @return    the type of this TLV, as determined from the underlying array.
   *            Never <code>null</code>.
   *
   * @throws RuntimeException   if the type constant cannot be determined.
   *         To allow for skipping TLVs with unknown types,
   *         implementations may map those types to a special constant.
   */
  protected abstract T determineType()
    throws RuntimeException
    ;


  /**
   * Determines the <i>length</i> of this TLV.
   * Called by {@link #update}.
   *
   * @return    the length of this TLV, as determined from the underlying array
   *
   * @throws RuntimeException   if the length cannot be determined
   */
  protected abstract int determineLength()
    ;


  /**
   * Obtains the offset of the value from the start of this TLV.
   *
   * @return    the number of bytes occupied by <i>type</i> and <i>length</i>
   */
  protected abstract int getValueOffset()
    ;


  // non-javadoc, see interface
  public final T getType()
  {
    return tlvType;
  }


  // non-javadoc, see interface
  public final int getSize()
  {
    return getValueOffset() + valueLength;
  }


  // non-javadoc, see interface
  public final int getLength()
  {
    return valueLength;
  }


  // non-javadoc, see interface
  public final byte[] getData()
  {
    return tlvData;
  }


  // non-javadoc, see interface
  public final int getStart()
  {
    return tlvStart;
  }


  // non-javadoc, see interface
  public final int getValueStart()
  {
    return tlvStart + getValueOffset();
  }


  // non-javadoc, see interface
  public final int getEnd()
  {
    return getValueStart() + valueLength;
  }


  // non-javadoc, see interface
  public final byte[] copyValue()
  {
    byte[] result = new byte[valueLength];
    System.arraycopy(tlvData, getValueStart(), result, 0, valueLength);
    return result;
  }


  // non-javadoc, see interface
  public final byte[] copyTLV()
  {
    byte[] result = new byte[getSize()];
    System.arraycopy(tlvData, tlvStart, result, 0, result.length);
    return result;
  }


  // non-javadoc, see interface
  public final ByteBuffer toBuffer()
  {
    return ByteBuffer.wrap(tlvData, tlvStart, getSize());
  }


  /**
   * Generates an extended string representation of this TLV, including data.
   * This relies on the cached length, call {@link #update} to refresh.
   */
  public final String toFullString()
  {
    StringBuilder sb = new StringBuilder(30+3*valueLength);

    sb.append("TLV(");
    if (tlvType != null)
       sb.append(tlvType);
    else
       appendHex(sb, tlvData[tlvStart]);
    sb.append(':').append(valueLength);

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


  public final static String HEX = "0123456789abcdef";
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
