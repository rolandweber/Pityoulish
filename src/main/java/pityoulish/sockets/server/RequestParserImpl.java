/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.sockets.tlv.MsgBoardTLV;
import pityoulish.sockets.tlv.MsgBoardType;


/**
 * Implementation of {@link RequestParser} for the Binary Protocol.
 */
public class RequestParserImpl implements RequestParser
{
  // non-javadoc, see interface
  public MsgBoardRequest parse(byte[] data, int start, int end)
    throws ProtocolException
  {
    if (data == null)
       throw new NullPointerException("data");
    // need at least 4 bytes for a valid MsgBoardTLV
    if (data.length < 4)
       throw new IllegalArgumentException
         ("data.length="+data.length);
    if ((start < 0) || (start+4 > data.length))
       throw new IllegalArgumentException
         ("start="+start+",data.length="+data.length);
    if (end < start+4)
       throw new IllegalArgumentException
         ("end="+end+",start="+start);
    // end may reach beyond the data

    // At this point, we know there are at least 4 byte of data,
    // so we can try to interpret that as a MsgBoardTLV.
    MsgBoardTLV reqtlv = getRequestTLV(data, start, end);

    MsgBoardRequest result = null;
    switch (reqtlv.getType())
     {
      case LIST_MESSAGES:
        result = parseListMessages(reqtlv);
        break;

      case PUT_MESSAGE:
        result = parsePutMessage(reqtlv);
        break;

      case OBTAIN_TICKET:
        result = parseObtainTicket(reqtlv);
        break;

      case RETURN_TICKET:
        result = parseReturnTicket(reqtlv);
        break;

      case REPLACE_TICKET:
        result = parseReplaceTicket(reqtlv);
        break;

      default:
       //@@@ implement NLS light
        throw new ProtocolException
          ("Invalid type of top-level TLV: "+reqtlv.getType());
     }

    if (result == null)
       throw new UnsupportedOperationException("@@@ not yet implemented");

    return result;

  } // parse


  /**
   * Creates the top-level TLV and performs some validations.
   *
   * @param data    array containing the data to parse
   * @param start   index of the first byte of data to parse
   * @param end     index after the last byte of data to parse
   *
   * @return    the constructed TLV representing the request,
   *            never <code>null</code>
   *
   * @throws ProtocolException  in case of invalid request data
   */
  protected MsgBoardTLV getRequestTLV(byte[] data, int start, int end)
    throws ProtocolException
  {
    // arguments already checked in parse(...) above

    MsgBoardTLV result = null;

    try {
      result = new MsgBoardTLV(data, start);

    } catch (RuntimeException rtx) {
      //@@@ implement NLS light
      throw new ProtocolException("Invalid top-level TLV header.", rtx);
    }

    if (result.getEnd() > end)
       //@@@ implement NLS light
       throw new ProtocolException("Invalid length of top-level TLV.");

    if (result.getEnd() > data.length)
       //@@@ implement NLS light
       throw new ProtocolException("Incomplete data for top-level TLV.");

    // We could check for a valid type here. But the caller is going to
    // switch on the type anyway, so it's simpler to check it there.

    return result;
  }


  protected MsgBoardRequest parseListMessages(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    Integer limit = null;
    String  marker = null;

    for (MsgBoardTLV nested = reqtlv.getNestedTLV();
         nested != null;
         nested = nested.getNextTLV(reqtlv.getEnd())
         )
     {
       if (nested.getEnd() > reqtlv.getEnd())
          throw failOverlongTLV(nested);

       switch (nested.getType())
        {
         case LIMIT:
           if (limit != null)
              throw failDuplicateTLV(nested);
           limit = new Integer(parseLimit(nested));
           break;

         case MARKER:
           if (marker != null)
              throw failDuplicateTLV(nested);
           marker = parseStringValue(nested, "US-ASCII");
           // protocol specifies printable characters, could check that too
           break;

         default:
           throw failUnexpectedTLV(nested);
        }
     }

    if (limit == null)
       throw new ProtocolException("missing "+MsgBoardType.LIMIT+
                                   " in TLV "+reqtlv.getType());

    return MsgBoardRequestImpl.newListMessages(limit, marker);
  }


  protected MsgBoardRequest parsePutMessage(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }

  protected MsgBoardRequest parseObtainTicket(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }

  protected MsgBoardRequest parseReturnTicket(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }

  protected MsgBoardRequest parseReplaceTicket(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  //@@@ Implement a generic parsing method for requests with all-mandatory
  //@@@ arguments. Pass EnumSet of mandatory arguments.
  //@@@ Means I cannot use some of the helpers in MsgBoardRequestImpl. Too bad.


  /**
   * Parses the value of a {@link MsgBoardType#LIMIT LIMIT} TLV.
   *
   * @param tlv   the TLV to parse
   *
   * @return the limit
   *
   * @throws ProtocolException if the value is invalid
   */
  protected int parseLimit(MsgBoardTLV tlv)
    throws ProtocolException
  {
    if (tlv.getLength() != 1)
       //@@@ implement NLS light
       throw new ProtocolException("invalid length of TLV "+tlv.getType()+
                                   " at position "+tlv.getStart());

    int limit = tlv.getData()[tlv.getValueStart()] & 0xff;
    if ((limit < 1) || (limit > 127))
       //@@@ implement NLS light
       throw new ProtocolException("invalid value "+limit+
                                   " of TLV "+tlv.getType()+
                                   " at position "+tlv.getStart());

    return limit;
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
   * @throws ProtocolException if the value is invalid
   */
  //@@@ support an optional Regex pattern, for additional validation?
  protected String parseStringValue(MsgBoardTLV tlv, String enc)
    throws ProtocolException
  {
    if (tlv.getLength() < 1)
       return "";

    if (enc == null)
       enc = "UTF-8";

    String result = null;
    try {
      result = new String(tlv.getData(), tlv.getValueStart(),
                          tlv.getLength(), enc);
    } catch (Exception x) {
      // UnsupportedEncodingException is not expected here
      // just assume that the value is wrong, rather than the encoding
      //@@@ implement NLS light
      throw new ProtocolException("invalid string value of TLV "+tlv.getType()+
                                  " at position "+tlv.getStart()+
                                  ", expected encoding "+enc,
                                  x);
    }
    return result;
  }



  /**
   * Creates an exception about an unexpected TLV.
   * The exception is returned, so it can be thrown by the caller.
   *
   * @param tlv   the unexpected TLV
   *
   * @return an exception describing the problem
   */
  protected ProtocolException failUnexpectedTLV(MsgBoardTLV tlv)
  {
    //@@@ implement NLS light
    return new ProtocolException("unexpected TLV "+tlv.getType()+
                                 " at position "+tlv.getStart());
  }


  /**
   * Creates an exception about a TLV too long for the containing value.
   * The exception is returned, so it can be thrown by the caller.
   *
   * @param tlv   the overlong TLV
   *
   * @return an exception describing the problem
   */
  protected ProtocolException failOverlongTLV(MsgBoardTLV tlv)
  {
    //@@@ implement NLS light
    return new ProtocolException("TLV "+tlv.getType()+
                                 " at position "+tlv.getStart()+
                                 " too long for containing value");
  }


  /**
   * Creates an exception about a duplicate TLV in a value.
   * The exception is returned, so it can be thrown by the caller.
   *
   * @param tlv   the duplicate TLV
   *
   * @return an exception describing the problem
   */
  protected ProtocolException failDuplicateTLV(MsgBoardTLV tlv)
  {
    //@@@ implement NLS light
    return new ProtocolException("duplicate TLV "+tlv.getType()+
                                 " at position "+tlv.getStart());
  }


}
