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
 * Implementation of {@link RequestBuilder} for the Binary Protocol.
 */
public class TLVRequestBuilderImpl implements RequestBuilder
{
  // public default constructor


  /**
   * Estimate the data size for a request with a single string.
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
    int estimate = 8; // request TLV and nested TLV header

    if (s != null)
     {
       // Encoding a Unicode character in UTF may require up to three byte
       final int factor = utf8 ? 3 : 1;
       estimate += s.length() * factor;
     }

    return estimate;
  }


  // non-javadoc, see interface
  public ByteBuffer buildListMessages(int limit, String marker)
  {
    int estimate = estimateLength(marker, false) + 5;
    byte[] data = new byte[estimate];

    MsgBoardTLV request = new MsgBoardTLV(MsgBoardType.LIST_MESSAGES, data, 0);
    MsgBoardTLV param = request.appendTLV(MsgBoardType.LIMIT);
    param.setLength(1);
    data[param.getValueStart()] = (byte)limit;
    request.addToLength(param.getSize());

    if (marker != null)
     {
       param = request.appendTLV(MsgBoardType.MARKER);
       param.setTextValue(marker, "US-ASCII");
       request.addToLength(param.getSize());
     }

    return request.toBuffer();
  }


  // non-javadoc, see interface
  public ByteBuffer buildPutMessage(String ticket, String text)
  {
    // pass UTF-8 text to helpers, deal with ASCII ticket locally
    int estimate = estimateLength(text, true) + 4 + ticket.length();
    MsgBoardTLV request = buildSingleStringRequest(estimate,
                                                   MsgBoardType.PUT_MESSAGE,
                                                   MsgBoardType.TEXT,
                                                   text, true);
    // now add the ticket
    MsgBoardTLV param = request.appendTLV(MsgBoardType.TICKET);
    param.setTextValue(ticket, "US-ASCII");
    request.addToLength(param.getSize());

    return request.toBuffer();
  }


  // non-javadoc, see interface
  public ByteBuffer buildObtainTicket(String username)
  {
    return buildSingleStringRequest(0,
                                    MsgBoardType.OBTAIN_TICKET,
                                    MsgBoardType.ORIGINATOR,
                                    username, false
                                    ).toBuffer();
  }


  // non-javadoc, see interface
  public ByteBuffer buildReturnTicket(String ticket)
  {
    return buildSingleStringRequest(0,
                                    MsgBoardType.RETURN_TICKET,
                                    MsgBoardType.TICKET,
                                    ticket, false
                                    ).toBuffer();
  }


  // non-javadoc, see interface
  public ByteBuffer buildReplaceTicket(String ticket)
  {
    return buildSingleStringRequest(0,
                                    MsgBoardType.REPLACE_TICKET,
                                    MsgBoardType.TICKET,
                                    ticket, false
                                    ).toBuffer();
  }


  /**
   * Builds a request with a single string as argument.
   * The caller may choose to append more arguments afterwards.
   *
   * @param estimate    the estimated length; 0 or negative for none.
   *                    If positive, the value MUST be sufficient to hold
   *                    the complete TLV.
   * @param reqtype     the type of the request TLV to build
   * @param argtype     the type of the argument TLV to include
   * @param arg         the argument string to include
   * @param utf8        <code>true</code> for UTF-8 encoding,
   *                    <code>false</code> for US-ASCII
   *
   * @return a TLV constructed from the arguments.
   *    If <code>estimate</code> was positive, the underlying byte array
   *    has exactly that size.
   */
  protected MsgBoardTLV buildSingleStringRequest(int estimate,
                                                 MsgBoardType reqtype,
                                                 MsgBoardType argtype,
                                                 String arg, boolean utf8)
  {
    if (estimate < 1)
       estimate = estimateLength(arg, utf8);

    byte[] data = new byte[estimate];

    MsgBoardTLV request = new MsgBoardTLV(reqtype, data, 0);
    MsgBoardTLV param = request.appendTLV(argtype);
    param.setTextValue(arg, utf8 ? "UTF-8" : "US-ASCII");
    request.addToLength(param.getSize());

    //System.out.println("@@@ "+request.toFullString());

    return request;
  }

}

