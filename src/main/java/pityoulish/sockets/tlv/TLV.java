/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;

import java.nio.ByteBuffer;


/**
 * Represents a TLV structure referring to a byte array.
 * The TLV does not necessarily begin at the first byte in the array.
 * Types are represented by a class, typically <!-- pun intended -->
 * an enumeration.
 * <br/>
 * Implementations of this interface may cache information about
 * the type and length of the TLV.
 * If the TLV is changed through API methods of derived interfaces or classes,
 * the cached data is updated automatically.
 * However, if you manipulate the underlying byte array directly,
 * call {@link #update} explicitly to invalidate cached data.
 */
public interface TLV<T>
{
  /**
   * Obtains the <i>type</i>.
   *
   * @return    a constant object representing the type of this TLV.
   *            Never <code>null</code>.
   */
  public T getType()
    ;


  /**
   * Obtains the size of the TLV structure, in bytes.
   *
   * @return    the size of this TLV, in bytes.
   *            This is the overall size, including type and length bytes.
   */
  public int getSize()
    ;


  /**
   * Obtains the <i>length</i> of the value of the TLV structure, in bytes.
   *
   * @return    the <i>length</i> of the value of this TLV, in bytes.
   *            This is a net length, excluding type and length bytes.
   */
  public int getLength()
    ;


  /** Obtains the underlying data array. */
  public byte[] getData()
    ;


  /** Obtains the index of the first byte of this TLV. */
  public int getStart()
    ;


  /** Obtains the index of the first byte of the value. */
  public int getValueStart()
    ;


  /** Obtains the index after the last byte of this TLV. */
  public int getEnd()
    ;


  /** Updates cached data from the underlying byte array. */
  public void update()
    ;


  /**
   * Copies the value of this TLV into a new byte array.
   */
  public byte[] copyValue()
    ;


  /**
   * Copies this TLV into a new byte array.
   */
  public byte[] copyTLV()
    ;


  /**
   * Obtains a byte buffer with this TLV.
   * The buffer is backed by the same {@link #getData data} as this TLV.
   * The position is at the {@link #getStart start} of this TLV,
   * the limit at the {@link #getEnd end} of the value.
   *
   * @return    a byte buffer containing this TLV
   */
  public ByteBuffer toBuffer()
    ;


  /**
   * Generates an extended string representation of this TLV, including data.
   * Intended for debug output.
   */
  public String toFullString()
    ;

}
