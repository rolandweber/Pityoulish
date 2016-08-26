/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.nio.ByteBuffer;

import pityoulish.sockets.tlv.MsgBoardTLV;
import pityoulish.sockets.tlv.MsgBoardType;


/**
 * Implementation of {@link ResponseParser} for the Binary Protocol.
 */
public class TLVResponseParserImpl implements ResponseParser
{

  // non-javadoc, see interface
  public void parse(ByteBuffer response, Visitor visitor)
    throws Exception
  {
    if (response == null)
       throw new NullPointerException("ByteBuffer");
    if (visitor == null)
       throw new NullPointerException("ResponseParser.Visitor");

    MsgBoardTLV tlv = new MsgBoardTLV
      (response.array(), response.position()+response.arrayOffset());
    System.out.println(tlv.toFullString()); //@@@

    //@@@ while parsing, make sure we don't reach beyond the buffer limit!

    switch (tlv.getType())
     {
      case INFO_RESPONSE: {
        String text =
          parseNestedStringValue(tlv, MsgBoardType.TEXT, null);
        visitor.visitInfo(text);
      } break;

      case ERROR_RESPONSE: {
        String text =
          parseNestedStringValue(tlv, MsgBoardType.TEXT, null);
        visitor.visitError(text);
      } break;

      case TICKET_GRANT: {
        String ticket =
          parseNestedStringValue(tlv, MsgBoardType.TICKET, "US-ASCII");
        visitor.visitTicketGrant(ticket);
      } break;

      default:
        throw new UnsupportedOperationException("@@@ not yet implemented");
        //@@@ or unexpected/unsupported/invalid response type
     }
  } // parse


  /**
   * Parses a TLV with a single string value.
   *
   * @param parent  the TLV of which to parse the content
   * @param expect  the expected type of the contained TLV
   * @param enc     the expected encoding, for example "US-ASCII",
   *                or <code>null</code> for "UTF-8"
   *
   * @return    the string value of the contained TLV
   *
   * @throws Exception in case of a problem
   */
  protected String parseNestedStringValue(MsgBoardTLV  parent,
                                          MsgBoardType expect,
                                          String enc)
    throws Exception
  {
    //@@@ check for end of data!
    MsgBoardTLV nested = parent.getNestedTLV();
    if (nested.getType() != expect)
       throw new Exception("@@@ unexpected nested type: "
                           +nested.getType()+"!="+expect); //@@@ NLS

    return parseStringValue(nested, enc);
  }


  /**
   * Parses the string value of a TLV.
   *
   * @param tlv   the TLV with expected string value
   * @param enc   the expected encoding, for example "US-ASCII",
   *              or <code>null</code> for "UTF-8"
   *
   * @return the string value, or an empty string if the value is empty
   *
   * @throws Exception in case of a problem
   */
  protected String parseStringValue(MsgBoardTLV tlv, String enc)
    throws Exception
  {
    if (tlv.getLength() < 1)
       return "";

    if (enc == null)
       enc = "UTF-8";

    return new String(tlv.getData(), tlv.getValueStart(),
                      tlv.getLength(), enc);
  }

}
