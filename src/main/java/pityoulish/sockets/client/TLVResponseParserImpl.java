/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.nio.ByteBuffer;

import pityoulish.outtake.Missing;
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

      case MESSAGE_BATCH: {
        parseMessageBatch(tlv, visitor);
      } break;

      case TICKET_GRANT: {
        String ticket = null;

        // PYL:keep
        Missing.here("parse the Ticket Grant response");
        // Refer to the specification of the Binary Protocol for details.
        // Watch out for the correct encoding of the ticket.
        // PYL:cut
        ticket = parseNestedStringValue(tlv, MsgBoardType.TICKET, "US-ASCII");
        // PYL:end

        visitor.visitTicketGrant(ticket);
      } break;

      default:
        throw new UnsupportedOperationException
          (Catalog.INVALID_TOP_TLV_TYPE_1.format(tlv.getType()));
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
    MsgBoardTLV nested = parent.getNestedTLV();
    if (nested == null)
       throw new Exception
         (Catalog.MISSING_NESTED_TLV_3.format(parent.getType(),
                                              parent.getStart(),
                                              expect));
    if (nested.getType() != expect)
       throw new Exception
         (Catalog.UNEXPECTED_TLV_3.format(nested.getType(),
                                          nested.getStart(),
                                          expect));
    //@@@ check for end of data!

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


  /**
   * Parses a message batch and calls the visitor.
   *
   * @param mbtlv       the TLV with the message batch
   * @param visitor     the visitor to invoke on the message batch elements
   *
   * @throws Exception in case of a problem
   */
  protected void parseMessageBatch(MsgBoardTLV mbtlv, Visitor visitor)
    throws Exception
  {
    // The binary protocol specifies an order for the nested TLVs.
    // The Marker and an optional Missed indicator appear before the messages.

    MsgBoardTLV nested = mbtlv.getNestedTLV();
    if (nested == null)
       throw new Exception
         (Catalog.MISSING_NESTED_TLV_3.format(mbtlv.getType(),
                                              mbtlv.getStart(),
                                              MsgBoardType.MARKER));
    if (nested.getType() != MsgBoardType.MARKER)
       throw new Exception
         (Catalog.UNEXPECTED_TLV_3.format(nested.getType(),
                                          nested.getStart(),
                                          MsgBoardType.MARKER));
    String marker = parseStringValue(nested, "US-ASCII");

    boolean missed = false;
    nested = nested.getNextTLV(mbtlv.getEnd());
    if ((nested != null) && (nested.getType() == MsgBoardType.MISSED))
     {
       missed = true;
       nested = nested.getNextTLV(mbtlv.getEnd());
     }

    visitor.enterMessageBatch(marker, missed);

    while (nested != null)
     {
       if (nested.getType() != MsgBoardType.MESSAGE)
          throw new Exception
            (Catalog.UNEXPECTED_TLV_3.format(nested.getType(),
                                             nested.getStart(),
                                             MsgBoardType.MESSAGE));
       //@@@ check for end of data!

       parseMessage(nested, visitor);
       nested = nested.getNextTLV(mbtlv.getEnd());
     }

    visitor.leaveMessageBatch();
  }


  /**
   * Parses a message and calls the visitor.
   *
   * @param msgtlv      the TLV with the message
   * @param visitor     the visitor to invoke on the message
   *
   * @throws Exception in case of a problem
   */
  protected void parseMessage(MsgBoardTLV msgtlv, Visitor visitor)
    throws Exception
  {
    //System.out.println("@@@ parsing MESSAGE at "+msgtlv.getStart());

    // The order of elements within the message is not specified.
    // Parse them as they come, then check if something is missing.
    String originator = null;
    String timestamp = null;
    String text = null;

    MsgBoardTLV nested = msgtlv.getNestedTLV();
    while (nested != null)
     {
       //System.out.println("@@@ found "+nested.getType());

       //@@@ check for end of data!
       switch (nested.getType())
        {
         case ORIGINATOR:
           if (originator != null)
              throw new Exception
                (Catalog.DUPLICATE_TLV_2.format(nested.getType(),
                                                nested.getStart()));
           originator = parseStringValue(nested, "US-ASCII");
           break;

         case TIMESTAMP:
           if (timestamp != null)
              throw new Exception
                (Catalog.DUPLICATE_TLV_2.format(nested.getType(),
                                                nested.getStart()));
           timestamp = parseStringValue(nested, "US-ASCII");
           break;

         case TEXT:
           if (text != null)
              throw new Exception
                (Catalog.DUPLICATE_TLV_2.format(nested.getType(),
                                                nested.getStart()));
           text = parseStringValue(nested, "UTF-8");
           break;

         default:
           throw new Exception
             (Catalog.UNEXPECTED_TLV_2.format(nested.getType(),
                                              nested.getStart()));
        }
       nested = nested.getNextTLV(msgtlv.getEnd());
     }

    if (originator == null)
       throw new Exception
         (Catalog.MISSING_NESTED_TLV_3.format(msgtlv.getType(),
                                              msgtlv.getStart(),
                                              MsgBoardType.ORIGINATOR));
    if (timestamp == null)
       throw new Exception
         (Catalog.MISSING_NESTED_TLV_3.format(msgtlv.getType(),
                                              msgtlv.getStart(),
                                              MsgBoardType.TIMESTAMP));
    if (text == null)
       throw new Exception
         (Catalog.MISSING_NESTED_TLV_3.format(msgtlv.getType(),
                                              msgtlv.getStart(),
                                              MsgBoardType.TEXT));

    visitor.visitMessage(originator, timestamp, text);
  }

}
