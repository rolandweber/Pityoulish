/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.nio.ByteBuffer;

import pityoulish.sockets.tlv.MsgBoardTLV;


/**
 * Implementation of {@link ResponseParser} for the Binary Protocol.
 */
public class TLVResponseParserImpl implements ResponseParser
{

  // non-javadoc, see interface
  public void parse(ByteBuffer response, Visitor visitor)
    throws Exception
  {
    System.out.println("@@@ "+response); //@@@
    MsgBoardTLV tlvrsp = new MsgBoardTLV
      (response.array(), response.position()+response.arrayOffset());
    System.out.println(tlvrsp.toFullString()); //@@@
    throw new UnsupportedOperationException("@@@ should parse response now");
  }

}
