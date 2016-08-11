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
   * Builds a TLV structure.
   *
   * @param input       some optional strings to replace default ones
   *
   * @return            the binary TLV data structure
   */
  public static byte[] buildTLV(String... input)
  {
    String text       = "Are you the Creator?";
    String originator = "V'Ger";
    String timestamp  = "2273-04-01T12:34:56";

    if (input.length > 0)
       text = input[0];
    if (input.length > 1)
       originator = input[1];
    if (input.length > 2)
       timestamp = input[2];

    // Need to guess the size of the resulting array.
    // Each TLV has 4 bytes of overhead for T and L.
    // There's the main TLV, and three nested TLVs.
    int needed = 16;
    // The space needed for the characters depends on the character encoding.
    // In the worst case of the UTF-8 encoding, a character may need 4 bytes.
    needed += 4 * text.length();
    needed += 4 * originator.length();
    needed += 4 * timestamp.length();

    // create an array and build the main TLV with length 0
    byte[] data = new byte[needed];
    BuildTLV main = new MsgBoardTLV(MsgBoardType.MESSAGE, data, 0);
    System.out.println("main TLV before adding nested TLVs:");
    System.out.println(main.toFullString());

    // add the first nested TLV to the value:
    // - construct the TLV at the end of main TLV
    // - update the length of the main TLV to include the nested one

    BuildTLV next = new MsgBoardTLV(MsgBoardType.TEXT, data, main.getEnd());
    next.setTextValue(text, "UTF-8");
    // There is a convenience method for text values.
    // For non-text data, encode it yourself into the 'data' array,
    // starting at position next.getValueStart() or next.getEnd().
    // Both values are identical as long as the value has length 0.
    // When you're done, call next.setLength(n) to update the length.

    // 'length' is the length of the value only, while
    // 'size' is the size of the whole TLV, including T and L.
    // The value of 'main' is augmented by the whole of 'next'.
    main.addToLength(next.getSize());
    System.out.println("added "+next.toFullString());

    // again for the originator
    next = new MsgBoardTLV(MsgBoardType.ORIGINATOR, data, main.getEnd());
    next.setTextValue(originator, "UTF-8");
    main.addToLength(next.getSize());
    System.out.println("added "+next.toFullString());

    // and again for the timestamp
    next = new MsgBoardTLV(MsgBoardType.TIMESTAMP, data, main.getEnd());
    next.setTextValue(timestamp, "UTF-8");
    main.addToLength(next.getSize());
    System.out.println("added "+next.toFullString());


    System.out.println("main TLV with all nested TLVs:");
    System.out.println(main.toFullString());

    // Most likely, the main TLV does not use the whole data array.
    // That doesn't matter for parsing though, because the actual length
    // is encoded in the TLV. If you have to send the data somewhere,
    // main.getEnd() tells you where to stop.

    return data;

  } // buildTLV

}
