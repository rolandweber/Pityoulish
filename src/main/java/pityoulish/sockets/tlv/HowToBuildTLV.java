/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;


/**
 * Shows how to use the {@link BuildTLV} interface.
 * Look at the comments in the
 * <a href="HowToBuildTLV.java.txt">source code</a>
 * for explanations.
 */
public class HowToBuildTLV
{
  public static void main(String[] args)
  {
    byte[] data = buildTLV(args);

    System.out.println();
  }


  /**
   * Builds a TLV structure from an array of {@link String}.
   *
   * @param msgs        the array to turn into a TLV structure
   *
   * @return            the binary TLV data structure
   */
  public static byte[] buildTLV(String[] msgs)
  {
    // Need to guess the size of the resulting array.
    // Each TLV has 4 bytes of overhead for T and L.
    // There's the main TLV, and one nested TLV for each string.
    int needed = 4 + 4 * msgs.length;
    // The space needed for the characters depends on the character encoding.
    // In the worst case of the UTF-8 encoding, a character may need 4 bytes.
    for (String msg : msgs)
       needed += msg.length()*4;

    // create an array and build the main TLV with length 0
    byte[] data = new byte[needed];
    //@@@ example from pre-OSS code not yet adapted to the binary protocol!
    //@@@ choose a different example anyway... LIST_MESSAGES maybe?
    BuildTLV main = new MsgBoardTLV(MsgBoardType.MESSAGE_BATCH, data, 0);
    System.out.println("main TLV before adding messages:");
    System.out.println(main.toFullString());

    // now add the nested TLVs one by one
    for (String msg : msgs)
     {
       BuildTLV next = new MsgBoardTLV(MsgBoardType.TEXT, data, main.getEnd());
       next.setTextValue(msg, "UTF-8");
       // For non-text data, encode it yourself into the 'data' array,
       // starting at position next.getValueStart() or next.getEnd().
       // Both values are identical as long as the value has length 0.
       // When you're done, call next.setLength(n) to update the length.

       // 'length' is the length of the value only, while
       // 'size' is the size of the whole TLV, including T and L.
       // The value of 'main' is augmented by the whole of 'next'.
       main.addToLength(next.getSize());

       System.out.println("added "+next.toFullString());
     }

    System.out.println("main TLV with all messages:");
    System.out.println(main.toFullString());

    // Most likely, the main TLV does not use the whole data array.
    // That doesn't matter for parsing though, because the actual length
    // is encoded in the TLV. If you have to send the data somewhere,
    // main.getEnd() tells you where to stop.

    return data;

  } // buildTLV

}
