/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;


/**
 * Represents a TLV of the binary protocol for Message Boards.
 * This class supports only the restricted BER format of the binary protocol.
 * In particular, the type is encoded as a single byte
 * and the length with exactly three bytes.
 */
public class MsgBoardTLV extends AbstractTLV<MsgBoardType>
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
   * Creates a new TLV pointing to the provided data.
   * This does <i>not</i> initialize the cached type and length,
   * call {@link #update} if necessary.
   *
   * @param data        the byte array in which the structure is stored
   * @param pos         index of the type byte in the array
   */
  protected MsgBoardTLV(byte[] data, int pos)
  {
    super(data, pos);
  }


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


  protected int getValueOffset()
  {
    // 1 byte for the type
    // 3 byte for the length
    return 4;
  }

}
