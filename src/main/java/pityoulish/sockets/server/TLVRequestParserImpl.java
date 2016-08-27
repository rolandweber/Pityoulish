/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.util.EnumSet;
import java.util.EnumMap;

import pityoulish.sockets.tlv.MsgBoardTLV;
import pityoulish.sockets.tlv.MsgBoardType;


/**
 * Implementation of {@link RequestParser} for the Binary Protocol.
 */
public class TLVRequestParserImpl implements RequestParser
{
  // public default constructor


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
        throw Catalog.INVALID_TOP_TLV_TYPE_1.asPX(reqtlv.getType());
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
      throw Catalog.INVALID_TOP_TLV_HEADER_0.asPXwithCause(rtx);
    }

    if (result.getEnd() > end)
       throw Catalog.INVALID_TOP_TLV_LENGTH_0.asPX();

    if (result.getEnd() > data.length)
       throw Catalog.INCOMPLETE_TOP_TLV_DATA_0.asPX();

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
       throw Catalog.MISSING_NESTED_TLV_3.asPX
         (reqtlv.getType(), reqtlv.getStart(), MsgBoardType.LIMIT);

    return MsgBoardRequestImpl.newListMessages(limit, marker);
  }


  protected MsgBoardRequest parsePutMessage(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    return parseGeneric(MsgBoardRequest.ReqType.PUT_MESSAGE, reqtlv,
                        EnumSet.of(MsgBoardType.TICKET, MsgBoardType.TEXT));
  }

  protected MsgBoardRequest parseObtainTicket(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    return parseGeneric(MsgBoardRequest.ReqType.OBTAIN_TICKET, reqtlv,
                        EnumSet.of(MsgBoardType.ORIGINATOR));
  }

  protected MsgBoardRequest parseReturnTicket(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    return parseGeneric(MsgBoardRequest.ReqType.RETURN_TICKET, reqtlv,
                        EnumSet.of(MsgBoardType.TICKET));
  }

  protected MsgBoardRequest parseReplaceTicket(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    return parseGeneric(MsgBoardRequest.ReqType.REPLACE_TICKET, reqtlv,
                        EnumSet.of(MsgBoardType.TICKET));
  }


  /**
   * Parses any request with all-mandatory, all-string parameters.
   * Does not support non-string parameters.
   * Does not support optional parameters.
   *
   * @param retype      the type for the object to return
   * @param reqtlv      the request to parse
   * @param expected    the mandatory string parameters.
   *                    Only primitive TLV types that contain a string value
   *                    are supported in this set.
   *
   * @return the parsed request
   *
   * @throws ProtocolException  in case of a problem
   */
      protected MsgBoardRequest parseGeneric(MsgBoardRequest.ReqType retype,
                                             MsgBoardTLV reqtlv,
                                             EnumSet<MsgBoardType> expected)
    throws ProtocolException
  {
    EnumMap<MsgBoardType,String> params = new EnumMap<>(MsgBoardType.class);

    for (MsgBoardTLV nested = reqtlv.getNestedTLV();
         nested != null;
         nested = nested.getNextTLV(reqtlv.getEnd())
         )
     {
       if (nested.getEnd() > reqtlv.getEnd())
          throw failOverlongTLV(nested);
       if (!expected.contains(nested.getType()))
          throw failUnexpectedTLV(nested);
       if (params.containsKey(nested.getType()))
          throw failDuplicateTLV(nested);

       String value = null;
       switch (nested.getType())
        {
         case TEXT:
           value = parseStringValue(nested, "US-ASCII");
           break;

         case ORIGINATOR:
         case MARKER:
         case TICKET:
           value = parseStringValue(nested, "UTF-8");
           break;

           // LIMIT is not a string value
           // TIMESTAMP and MISSED do not appear in requests

         default:
           // can only happen if 'expected' contains an unsupported element
           throw new IllegalArgumentException
             ("EnumSet<MsgBoardType>::"+nested.getType());
        }

       params.put(nested.getType(), value);
     }

    // check for absence of a mandatory parameter
    EnumSet<MsgBoardType> missing = EnumSet.copyOf(expected);
    missing.removeAll(params.keySet());
    if (!missing.isEmpty())
     {
       // several could be missing, just pick one to report...
       throw Catalog.MISSING_NESTED_TLV_3.asPX
         (reqtlv.getType(), reqtlv.getStart(), missing.iterator().next());
     }

    return new MsgBoardRequestImpl
      (retype, null,
       params.get(MsgBoardType.MARKER),
       params.get(MsgBoardType.TICKET),
       params.get(MsgBoardType.TEXT),
       params.get(MsgBoardType.ORIGINATOR)
       );
  }


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
       throw Catalog.INVALID_TLV_LENGTH_2.asPX(tlv.getType(), tlv.getStart());

    int limit = tlv.getData()[tlv.getValueStart()] & 0xff;
    if ((limit < 1) || (limit > 127))
       throw Catalog.INVALID_TLV_VALUE_3.asPX
         (tlv.getType(), tlv.getStart(), limit);

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
  //@@@ support an optional Regex pattern for validation?
  protected String parseStringValue(MsgBoardTLV tlv, String enc)
    throws ProtocolException
  {
    if (tlv.getLength() < 1)
       return "";

    if (enc == null)
       enc = "UTF-8";

    String result = null;
    try {
      // The constructors of the String class replace invalid characters
      // with a special marker. Therefore, we won't get an exception here
      // if for example non-ASCII characters are parsed as US-ASCII.
      result = new String(tlv.getData(), tlv.getValueStart(),
                          tlv.getLength(), enc);
    } catch (Exception x) {
      // UnsupportedEncodingException is not expected here
      // just assume that the value is wrong, rather than the encoding

      throw Catalog.INVALID_TLV_STRING_ENC_3.asPXwithCause
        (x, tlv.getType(), tlv.getStart(), enc);
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
    return Catalog.UNEXPECTED_TLV_2.asPX(tlv.getType(), tlv.getStart());
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
    return Catalog.DUPLICATE_TLV_2.asPX(tlv.getType(), tlv.getStart());
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
    return Catalog.OVERLONG_TLV_2.asPX(tlv.getType(), tlv.getStart());
  }


}
