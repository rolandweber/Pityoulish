/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.sockets.tlv.MsgBoardTLV;


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

    // We could check for a valid type here. But the caller is going to
    // switch on the type anyway, so it's simpler to check it there.

    return result;
  }


  protected MsgBoardRequest parseListMessages(MsgBoardTLV reqtlv)
    throws ProtocolException
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
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


}
