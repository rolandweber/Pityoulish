/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.msgboard.MessageBatch;
import pityoulish.sockets.tlv.MsgBoardTLV;
import pityoulish.sockets.tlv.MsgBoardType;

/**
 * Implementation of {@link ResponseBuilder} for the Binary Protocol.
 */
public class TLVResponseBuilderImpl implements ResponseBuilder
{
  // public default constructor


  /**
   * Estimate the data size for a response with a single string.
   * The estimate is conservative, never too small.
   *
   * @param s           the string to send back, or <code>null</code>
   * @param utf8        <code>true</code> for UTF-8 encoding,
   *                    <code>false</code> for US-ASCII
   *
   * @return the estimated size of an array that can hold the response PDU
   */
  protected int estimateLength(String s, boolean utf8)
  {
    int estimate = 8; // response TLV and nested TLV header

    if (s != null)
     {
       // Encoding a Unicode character in UTF may require up to three byte
       final int factor = utf8 ? 3 : 1;
       estimate += s.length() * factor;
     }

    return estimate;
  }


  /**
   * Builds a response with a single string as the content.
   *
   * @param rsptype     type of the response TLV
   * @param txttype     type of the nested TLV
   * @param text        the string to put into the PDU.
   *                    <code>null</code> is treated as an empty string
   * @param utf8        <code>true</code> for UTF-8 encoding,
   *                    <code>false</code> for US-ASCII
   *
   * @return the data of the response PDU
   */
  protected byte[] buildSimpleResponsePDU(MsgBoardType rsptype,
                                          MsgBoardType txttype,
                                          String text,
                                          boolean utf8)
  {
    if (rsptype == null)
       throw new NullPointerException("rsptype");
    if (txttype == null)
       throw new NullPointerException("txttype");
    if (text == null)
       text = "";

    byte[]      data     = new byte[estimateLength(text, utf8)];
    MsgBoardTLV response = new MsgBoardTLV(rsptype, data, 0);
    MsgBoardTLV content  = response.appendTLV(txttype);
    content.setTextValue(text, utf8 ? "UTF-8" : "US-ASCII");
    response.addToLength(content.getSize());

    //@@@ provide a method for truncating the byte array in class BuildTLV,
    //@@@ or for getting a copy of the full TLV data

    byte[] result = null;
    if (data.length == response.getSize())
     {
       result = data;
     }
    else
     {
       result = new byte[response.getSize()];
       System.arraycopy(data, 0, result, 0, result.length);
     }

    //System.out.println("@@@ "+response.toFullString());

    return result;
  }


  // non-javadoc, see interface
  public byte[] buildInfoResponse(String msg)
  {
    return buildSimpleResponsePDU(MsgBoardType.INFO_RESPONSE,
                                  MsgBoardType.TEXT,
                                  msg, true);
  }


  // non-javadoc, see interface
  public byte[] buildErrorResponse(String msg)
  {
    return buildSimpleResponsePDU(MsgBoardType.ERROR_RESPONSE,
                                  MsgBoardType.TEXT,
                                  msg, true);
  }


  // non-javadoc, see interface
  public byte[] buildErrorResponse(Throwable cause)
  {
    //@@@ lame implementation, but see issue #9
    return buildSimpleResponsePDU(MsgBoardType.ERROR_RESPONSE,
                                  MsgBoardType.TEXT,
                                  String.valueOf(cause), true);
  }


  // non-javadoc, see interface
  public byte[] buildMessageBatch(MessageBatch msgbatch)
  {
    //@@@ estimating the size will be complicated

    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface
  public byte[] buildTicketGrant(String tictok)
  {
    return buildSimpleResponsePDU(MsgBoardType.TICKET_GRANT,
                                  MsgBoardType.TICKET,
                                  tictok, false);
  }

}
