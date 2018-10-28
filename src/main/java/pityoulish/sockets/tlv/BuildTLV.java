/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;

import java.io.UnsupportedEncodingException; // for JavaDoc


/**
 * Represents a TLV structure to be constructed.
 */
public interface BuildTLV<T> extends TLV<T>
{
  /**
   * Updates the length of this TLV.
   * Call this after constructing a new value in the underlying array,
   * to specify the length of the complete value.
   * <br>
   * <b>Warning:</b> Actually, this is not as simple as it may seem.
   * In general, the length field of a TLV is of variable size.
   * Therefore, setting the length may require relocation of the value
   * in the array. The binary protocol for Message Boards specifies
   * a fixed-size encoding for the length to avoid that.
   * <br>
   * For a general-purpose TLV implementation, it would also make sense
   * to determine and specify the length of the value <i>before</i>
   * actually constructing it. To support both use cases, the method would
   * require a parameter that indicates whether the value is already present
   * and has to be relocated.
   *
   * @param len         the new length
   */
  public void setLength(int len)
    ;


  /**
   * Changes the length of the value.
   * The argument delta is added to the current length.
   * Call this after appending a TLV to the value in the underlying array,
   * to add the length of that TLV.
   * <br>
   * See {@link #setLength} for a word about variable-size length encodings.
   *
   * @param delta       the change of length, typically positive
   *                    because data is added to the value
   */
  public void addToLength(int delta)
    ;


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
    ;


  /**
   * Creates a new TLV immediately after this one.
   * Its position in the underlying array depends on the current length
   * of this TLV. The new TLV is initially empty.
   * <br>
   * The length of <code>this</code> TLV remains unchanged.
   * You can create sequences of TLVs this way.
   * If you want to add the new TLV to the value of this TLV,
   * call {@link #addToLength} after building the value of the nested TLV.
   *
   * @param tlvT  the type of the TLV to create
   *
   * @return    a new TLV that resides immediately after this TLV.
   */
  public BuildTLV<T> appendTLV(T tlvT)
    ;

}
