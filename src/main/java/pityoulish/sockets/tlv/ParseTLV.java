/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;


/**
 * Represents a TLV structure to be parsed.
 * This interface provides convenience methods to iterate over the values
 * of constructed TLVs:
 * <pre><br>
 *     ParseTLV&lt;T&gt; constructedTLV = ...;
 *     for (ParseTLV&lt;T&gt; nestedTLV = constructedTLV.getNestedTLV();
 *          nestedTLV != null;
 *          nestedTLV = nestedTLV.getNextTLV(constructedTLV.getEnd())
 *          )
 *       {
 *         ... // do something with nestedTLV
 *       }
 * </pre>
 */
public interface ParseTLV<T> extends TLV<T>
{
  /**
   * Obtains a TLV for the value of this TLV.
   * If the value of this TLV is composed of several TLVs,
   * use {@link #getNextTLV} on the returned TLV to iterate.
   *
   * @return a TLV that is positioned at the beginning of this value,
   *         or <code>null</code> if not possible.
   *         Obtaining a nested TLV is not possible if the value is empty,
   *         or if the implementation knows that this TLV is primitive.
   *
   * @throws RuntimeException
   *         if creating the nested TLV throws an exception.
   *         Could be caused by invalid values.
   */
  public ParseTLV<T> getNestedTLV()
    throws RuntimeException
    ;


  /**
   * Obtains a TLV immediately after this TLV, if possible.
   * This is convenient to parse successive TLVs.
   *
   * @param eod   The index after the last byte of data to process
   *              (End Of Data).
   *              If this TLV already extends to the end of the data,
   *              <code>null</code> is returned.
   *              When iterating through the value of a constructed TLV,
   *              pass {@link TLV#getEnd constructedTLV.getEnd()} here.
   *
   * @return a TLV that is positioned at the end of this TLV,
   *         or <code>null</code> if already at the end of data.
   *
   * @throws RuntimeException
   *         if creating the next TLV throws an exception.
   *         Could be caused by invalid data.
   */
  public ParseTLV<T> getNextTLV(int eod)
    throws RuntimeException
    ;


}
