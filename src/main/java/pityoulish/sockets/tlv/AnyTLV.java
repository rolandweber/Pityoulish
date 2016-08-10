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
public class AnyTLV extends AbstractTLV<TLVType>
{
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
    super(data, pos);
  }


  protected TLVType determineType()
  {
    final byte tb = tlvData[tlvStart]; // type byte
    TLVType    te = null;              // type enum

    for (TLVType candidate : TLVType.values())
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
    if (tlvData[tlvStart+1] != ProtocolConstants.LENGTH_OF_LENGTH_2)
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
