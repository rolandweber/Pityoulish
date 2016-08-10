/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;


/**
 * Represents a TLV structure to be parsed.
 */
public class ParseTLV extends AnyTLV
{
  /**
   * Creates a new TLV pointing to the provided data.
   * The type and length are determined immediately.
   *
   * @param data        the byte array in which the structure is stored
   * @param pos         index of the type byte in the array
   */
  public ParseTLV(byte[] data, int pos)
  {
    super(data, pos);
    update();
  }

}
