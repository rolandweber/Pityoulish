/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

import pityoulish.msgboard.Message;
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
   * Estimates the data size for a response with a single string.
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
       // encoding a Unicode character in UTF-8 may require up to three byte
       final int factor = utf8 ? 3 : 1;
       estimate += s.length() * factor;
     }

    return estimate;
  }


  /**
   * Estimates the data size for a single message.
   * The estimate is conservative, never too small.
   *
   * @param msg         the message to send back
   *
   * @return the estimated size of a TLV that encodes that message
   */
  protected int estimateLength(Message msg)
  {
    int estimate = 16; // message TLV and three nested TLV

    if (msg != null)
     {
       // originator and timestamp are ASCII
       estimate += msg.getOriginator().length();
       estimate += msg.getTimestamp().length();

       // text is Unicode
       // encoding a Unicode character in UTF-8 may require up to three byte
       estimate += msg.getText().length() * 3;
     }

    return estimate;
  }


  /**
   * Appends a TLV-encoded string to the content of a TLV.
   *
   * @param parent      the TLV to append to
   * @param txttype     type of the TLV to append
   * @param text        the string to put into the nested TLV.
   *                    <code>null</code> is treated as an empty string
   * @param utf8        <code>true</code> for UTF-8 encoding,
   *                    <code>false</code> for US-ASCII
   */
  protected void appendString(MsgBoardTLV parent,
                              MsgBoardType txttype,
                              String text,
                              boolean utf8)
  {
    if (parent == null)
       throw new NullPointerException("MsgBoardTLV");
    if (txttype == null)
       throw new NullPointerException("txttype");
    if (text == null)
       text = "";

    MsgBoardTLV content = parent.appendTLV(txttype);
    content.setTextValue(text, utf8 ? "UTF-8" : "US-ASCII");
    parent.addToLength(content.getSize());
  }


  /**
   * Appends a TLV-encoded message to the content of a TLV.
   *
   * @param parent      the TLV to append to
   * @param msg         the message to append
   */
  protected void appendMessage(MsgBoardTLV parent, Message msg)
  {
    if (parent == null)
       throw new NullPointerException("MsgBoardTLV");
    if (msg == null)
       throw new NullPointerException("Message");

    MsgBoardTLV msgtlv = parent.appendTLV(MsgBoardType.MESSAGE);

    // The binary protocol doesn't specify the order in which to append
    // the three elements of a message. Just for the heck of it, randomize.

    MsgBoardType[] order = new MsgBoardType[]{
      MsgBoardType.ORIGINATOR, MsgBoardType.TIMESTAMP, MsgBoardType.TEXT
    };
    // determine which element to put first... three choices
    int first = ThreadLocalRandom.current().nextInt(3);
    if (first > 0)
     {
       MsgBoardType mbt = order[0];
       order[0] = order[first];
       order[first] = mbt;
     }
    // determine which element to put next... two choices
    if (ThreadLocalRandom.current().nextInt(2) > 0)
     {
       MsgBoardType mbt = order[1];
       order[1] = order[2];
       order[2] = mbt;
     }


    for (MsgBoardType mbt: order)
     {
       switch (mbt)
        {
         case ORIGINATOR:
           appendString(msgtlv, mbt, msg.getOriginator(), false);
           break;

         case TIMESTAMP:
           appendString(msgtlv, mbt, msg.getTimestamp(), false);
           break;

         case TEXT:
           appendString(msgtlv, mbt, msg.getText(), false);
           break;
        }
     }

    parent.addToLength(msgtlv.getSize());
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
   * @return a buffer containing the response PDU, backed by an array
   */
  protected ByteBuffer buildSimpleResponsePDU(MsgBoardType rsptype,
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
    appendString(response, txttype, text, utf8);

    //System.out.println("@@@ "+response.toFullString());

    return response.toBuffer();
  }


  // non-javadoc, see interface
  public ByteBuffer buildInfoResponse(String msg)
  {
    return buildSimpleResponsePDU(MsgBoardType.INFO_RESPONSE,
                                  MsgBoardType.TEXT,
                                  msg, true);
  }


  // non-javadoc, see interface
  public ByteBuffer buildErrorResponse(String msg)
  {
    return buildSimpleResponsePDU(MsgBoardType.ERROR_RESPONSE,
                                  MsgBoardType.TEXT,
                                  msg, true);
  }


  // non-javadoc, see interface
  public ByteBuffer buildErrorResponse(Throwable cause)
  {
    //@@@ lame implementation, but see issue #9
    return buildSimpleResponsePDU(MsgBoardType.ERROR_RESPONSE,
                                  MsgBoardType.TEXT,
                                  String.valueOf(cause), true);
  }


  // non-javadoc, see interface
  public ByteBuffer buildMessageBatch(MessageBatch msgbatch)
  {
    if (msgbatch == null)
       throw new NullPointerException("MessageBatch");

    int estimate = 8; // response TLV and nested marker
    estimate += msgbatch.getMarker().length(); // marker is ASCII
    if (msgbatch.isDiscontinuous())
       estimate += 4; // nested discontinuity indicator
    for (Message msg: msgbatch.getMessages())
       estimate += estimateLength(msg);

    byte[] data = new byte[estimate];
    MsgBoardTLV response =
      new MsgBoardTLV(MsgBoardType.MESSAGE_BATCH, data, 0);

    // The order of nested elements is specified by the protocol:
    // Marker, Missed (optional), Messages

    appendString(response, MsgBoardType.MARKER, msgbatch.getMarker(), false);

    if (msgbatch.isDiscontinuous())
     {
       MsgBoardTLV content = response.appendTLV(MsgBoardType.MISSED);
       // value is empty
       response.addToLength(content.getSize());
     }

    for (Message msg: msgbatch.getMessages())
     {
       appendMessage(response, msg);
     }

    //System.out.println("@@@ "+response.toFullString());

    return response.toBuffer();
  }


  // non-javadoc, see interface
  public ByteBuffer buildTicketGrant(String tictok)
  {
    return buildSimpleResponsePDU(MsgBoardType.TICKET_GRANT,
                                  MsgBoardType.TICKET,
                                  tictok, false);
  }

}
