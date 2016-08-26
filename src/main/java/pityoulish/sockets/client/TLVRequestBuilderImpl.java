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
    int estimate = 8; // response TLV and nested TLV header

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

    //System.out.println("@@@ "+request.toFullString());

    return request.toBuffer();
  }


  // non-javadoc, see interface
  public ByteBuffer buildPutMessage(String ticket, String text)
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface
  public ByteBuffer buildObtainTicket(String username)
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface
  public ByteBuffer buildReturnTicket(String ticket)
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface
  public ByteBuffer buildRefreshTicket(String ticket)
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }

}

