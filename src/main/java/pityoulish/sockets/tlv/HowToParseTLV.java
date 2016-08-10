/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;

import pityoulish.sockets.tlv.ProtocolConstants.TLVType;


/**
 * Shows how to use the {@link ParseTLV} class.
 * Look at the comments in the
 * <a href="HowToParseTLV.java.txt">source code</a>
 * for explanations.
 */
public class HowToParseTLV
{
  public static void main(String[] args)
  {
    // need something to parse...
    byte[] data = HowToBuildTLV.buildTLV(args);
    // There might be some extra bytes at the end of the array, but
    // that doesn't matter. Only the TLV content will be parsed.

    System.out.println();

    parseTLV(data);
  }


  /**
   * Parses a nested TLV structure.
   *
   * @param data        the encoded TLV structure
   */
  public static void parseTLV(byte[] data)
  {
    System.out.println("parsing from array of length "+data.length);

    // parsing a TLV interprets the tag and length
    ParseTLV main = new ParseTLV(data, 0);
    System.out.println(main.getType()+" at position "+main.getStart()+
                       ", value from "+main.getValueStart()+
                       " to "+(main.getEnd()-1));
    // getEnd() points to the position *after* the value

    // now loop over the nested TLVs that comprise the value of 'main'
    int pos = main.getValueStart();
    while (pos < main.getEnd())
     {
       ParseTLV next = new ParseTLV(data, pos);
       System.out.println(next.getType()+" at position "+next.getStart()+
                          ", value from "+next.getValueStart()+
                          " to "+(next.getEnd()-1));

       pos += next.getSize();
     }

    if (pos < data.length)
       System.out.println("ignoring data at position "+pos+" and beyond");
  }

}
