/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.sockets.server.MsgBoardRequest.ReqType;
import pityoulish.sockets.tlv.MsgBoardTLV;
import pityoulish.sockets.tlv.MsgBoardType;

import org.junit.*;
import static org.junit.Assert.*;


public class TLVRequestParserImplTest
{
  public final static String MSGCODE_PREFIX = "MBSS";

  /**
   * Checks for {@link ProtocolException} and certain elements of the message.
   * The message is expected to contain a message code.
   * If a <code>reason</code> is given, it must appear in the message too.
   *
   * @param cause    the exception to check
   * @param reason   a string expected in the message,
   *                 or <code>null</code> to skip this check.
   *                 Non-string arguments are converted to String first.
   */
  protected static void assertPX(Throwable cause, Object reason)
  {
    assertNotNull("no exception", cause);
    assertEquals("wrong exception",
                 ProtocolException.class,
                 cause.getClass());

    String msg = cause.getMessage();
    assertNotNull("no exception message", msg);
    assertNotEquals("empty exception message", "", msg);
    assertTrue("missing NLS code", msg.indexOf(MSGCODE_PREFIX) >= 0);

    if (reason != null)
     {
       String why = reason.toString();
       assertTrue("wrong reason for exception",
                  cause.getMessage().indexOf(why) >= 0);
     }
  }



  @Test public void parse_invalid_args()
    throws ProtocolException
  {
    RequestParser rp = new TLVRequestParserImpl();

    try {
      MsgBoardRequest mbr = rp.parse(null, 0, 8);
      fail("null data not detected: "+mbr);
    } catch (RuntimeException expected) {
      assertNotEquals("wrong exception",
                      UnsupportedOperationException.class,
                      expected.getClass());
    }

    byte[] data = new byte[2];
    try {
      MsgBoardRequest mbr = rp.parse(data, 0, 8);
      fail("short data not detected: "+mbr);
    } catch (RuntimeException expected) {
      assertNotEquals("wrong exception",
                      UnsupportedOperationException.class,
                      expected.getClass());
    }

    // the rest of the attempts is with enough data in the array
    data = new byte[8];

    try {
      MsgBoardRequest mbr = rp.parse(data, -1, 8);
      fail("negative start not detected: "+mbr);
    } catch (RuntimeException expected) {
      assertNotEquals("wrong exception",
                      UnsupportedOperationException.class,
                      expected.getClass());
    }

    try {
      MsgBoardRequest mbr = rp.parse(data, data.length-3, 8);
      fail("excessive start not detected: "+mbr);
    } catch (RuntimeException expected) {
      assertNotEquals("wrong exception",
                      UnsupportedOperationException.class,
                      expected.getClass());
    }

    try {
      MsgBoardRequest mbr = rp.parse(data, 3, 2);
      fail("end before start not detected: "+mbr);
    } catch (RuntimeException expected) {
      assertNotEquals("wrong exception",
                      UnsupportedOperationException.class,
                      expected.getClass());
    }

    try {
      MsgBoardRequest mbr = rp.parse(data, 3, 6);
      fail("end shortly after start not detected: "+mbr);
    } catch (RuntimeException expected) {
      assertNotEquals("wrong exception",
                      UnsupportedOperationException.class,
                      expected.getClass());
    }

    // the 'positive' case: we still get an exception, but
    // about invalid data rather than illegal arguments

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("invalid data not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, null);
    }

  } // parse_invalid_args


  @Test public void parse_invalid_header_Type()
    throws ProtocolException
  {
    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.INFO_RESPONSE.typeByte, // not a request type
      MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0,
      (byte) 0
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("invalid header not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.INFO_RESPONSE);
    }
  }


  @Test public void parse_invalid_header_LoL()
    throws ProtocolException
  {
    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      (byte) (MsgBoardTLV.LENGTH_OF_LENGTH_2+1), // only one LoL supported
      (byte) 0,
      (byte) 0,
      (byte) 0
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("invalid header not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, null);
    }
  }


  @Test public void parse_invalid_header_Length_end()
    throws ProtocolException
  {
    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0,
      (byte) 4,
      (byte) 0, (byte) 0, (byte) 0 // data too short, expect 4 byte
    };

    try {
      // end points to actual end of data
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("invalid header not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, null);
    }
  }


  @Test public void parse_invalid_header_Length_data()
    throws ProtocolException
  {
    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0,
      (byte) 4,
      (byte) 0, (byte) 0, (byte) 0 // data too short, expect 4 byte
    };

    try {
      // end matches the TLV size, but is beyond the available data
      MsgBoardRequest mbr = rp.parse(data, 0, data.length+1);
      fail("invalid header not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, null);
    }
  }



  @Test public void parseListMessages_LM()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(17);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 12,

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue(),

      MsgBoardType.MARKER.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 3,
      (byte) 'a', (byte) 'b', (byte) 'c'
    };

    MsgBoardRequest mbr = rp.parse(data, 0, data.length+1);

    assertNotNull("no result", mbr);
    assertEquals("wrong type", ReqType.LIST_MESSAGES, mbr.getReqType());
    assertEquals("wrong limit", limit,  mbr.getLimit());
    assertEquals("wrong marker", "abc", mbr.getMarker());

    assertNull("unexpected ticket",     mbr.getTicket());
    assertNull("unexpected text",       mbr.getText());
    assertNull("unexpected originator", mbr.getOriginator());
  }


  @Test public void parseListMessages_ML()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(17);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 12,

      MsgBoardType.MARKER.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 3,
      (byte) 'a', (byte) 'b', (byte) 'c',

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue()
    };

    MsgBoardRequest mbr = rp.parse(data, 0, data.length+1);

    assertNotNull("no result", mbr);
    assertEquals("wrong type", ReqType.LIST_MESSAGES, mbr.getReqType());
    assertEquals("wrong limit", limit,  mbr.getLimit());
    assertEquals("wrong marker", "abc", mbr.getMarker());

    assertNull("unexpected ticket",     mbr.getTicket());
    assertNull("unexpected text",       mbr.getText());
    assertNull("unexpected originator", mbr.getOriginator());
  }


  @Test public void parseListMessages_L()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(17);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 5,

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue()
    };

    MsgBoardRequest mbr = rp.parse(data, 0, data.length+1);

    assertNotNull("no result", mbr);
    assertEquals("wrong type", ReqType.LIST_MESSAGES, mbr.getReqType());
    assertEquals("wrong limit", limit,  mbr.getLimit());

    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected ticket",     mbr.getTicket());
    assertNull("unexpected text",       mbr.getText());
    assertNull("unexpected originator", mbr.getOriginator());
  }


  @Test public void parseListMessages_M_only()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(17);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 7,

      MsgBoardType.MARKER.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 3,
      (byte) 'a', (byte) 'b', (byte) 'c'
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("missing limit not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.LIMIT);
    }
  }


  @Test public void parseListMessages_L_none()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(17);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 0
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("missing limit not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.LIMIT);
    }
  }


  @Test public void parseListMessages_L_low()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(0);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 5,

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue()
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("bad limit not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.LIMIT);
    }
  }


  @Test public void parseListMessages_L_high()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(128);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 5,

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue()
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("bad limit not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.LIMIT);
    }
  }


  @Test public void parseListMessages_L_wide()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(80);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 6,

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 2, // length must be 1
      (byte) 0, limit.byteValue()
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("bad limit not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.LIMIT);
    }
  }


  @Test public void parseListMessages_M_excessive()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(17);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 12,

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue(),

      MsgBoardType.MARKER.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 8, // too long
      (byte) 'a', (byte) 'b', (byte) 'c',
      (byte) 'x', (byte) 'x', (byte) 'x', (byte) 'x', (byte) 'x'
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("bad marker not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.MARKER);
    }
  }


  @Test public void parseListMessages_LT()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(17);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 10,

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue(),

      MsgBoardType.TICKET.typeByte, // doesn't belong here
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      (byte) 'X'
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("bad marker not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.TICKET);
    }
  }


  @Test public void parseListMessages_LL()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(17);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 10,

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue(),

      MsgBoardType.LIMIT.typeByte, // duplicate
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue(),
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("bad marker not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.LIMIT);
    }
  }


  @Test public void parseListMessages_MLM()
    throws ProtocolException
  {
    final Integer limit = Integer.valueOf(26);

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.LIST_MESSAGES.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 19,

      MsgBoardType.MARKER.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 3,
      (byte) 'a', (byte) 'b', (byte) 'c',

      MsgBoardType.LIMIT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 1,
      limit.byteValue(),

      MsgBoardType.MARKER.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) 3,
      (byte) 'd', (byte) 'e', (byte) 'f',
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("bad marker not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.MARKER);
    }
  }


  @Test public void parsePutMessage_TicTxt()
    throws ProtocolException
  {
    final String ticket = "pass";
    final String text   = "whatever";

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.PUT_MESSAGE.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (8 + ticket.length() + text.length()),

      MsgBoardType.TICKET.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) ticket.length(),
      (byte)'p', (byte)'a', (byte)'s', (byte)'s',

      MsgBoardType.TEXT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) text.length(),
      (byte)'w', (byte)'h', (byte)'a', (byte)'t',
      (byte)'e', (byte)'v', (byte)'e', (byte)'r'
    };

    MsgBoardRequest mbr = rp.parse(data, 0, data.length+1);

    assertNotNull("no result", mbr);
    assertEquals("wrong type", ReqType.PUT_MESSAGE, mbr.getReqType());
    assertEquals("wrong ticket", ticket, mbr.getTicket());
    assertEquals("wrong text",   text,   mbr.getText());

    assertNull("unexpected limit",      mbr.getLimit());
    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected originator", mbr.getOriginator());
  }


  @Test public void parsePutMessage_TxtTic()
    throws ProtocolException
  {
    final String ticket = "pass";
    final String text   = "whatever";

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.PUT_MESSAGE.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (8 + ticket.length() + text.length()),

      MsgBoardType.TEXT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) text.length(),
      (byte)'w', (byte)'h', (byte)'a', (byte)'t',
      (byte)'e', (byte)'v', (byte)'e', (byte)'r',

      MsgBoardType.TICKET.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) ticket.length(),
      (byte)'p', (byte)'a', (byte)'s', (byte)'s'
    };

    MsgBoardRequest mbr = rp.parse(data, 0, data.length+1);

    assertNotNull("no result", mbr);
    assertEquals("wrong type", ReqType.PUT_MESSAGE, mbr.getReqType());
    assertEquals("wrong ticket", ticket, mbr.getTicket());
    assertEquals("wrong text",   text,   mbr.getText());

    assertNull("unexpected limit",      mbr.getLimit());
    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected originator", mbr.getOriginator());
  }


  @Test public void parsePutMessage_Tic()
    throws ProtocolException
  {
    final String ticket = "pass";
    // final String text   = "whatever"; // missing

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.PUT_MESSAGE.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (4 + ticket.length()),

      MsgBoardType.TICKET.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) ticket.length(),
      (byte)'p', (byte)'a', (byte)'s', (byte)'s'
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("missing text not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.TEXT);
    }
  }


  @Test public void parsePutMessage_Txt()
    throws ProtocolException
  {
    // final String ticket = "pass"; // missing
    final String text   = "whatever";

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.PUT_MESSAGE.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (4 + text.length()),

      MsgBoardType.TEXT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) text.length(),
      (byte)'w', (byte)'h', (byte)'a', (byte)'t',
      (byte)'e', (byte)'v', (byte)'e', (byte)'r'
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("missing ticket not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.TICKET);
    }
  }


  @Test public void parsePutMessage_none()
    throws ProtocolException
  {
    // final String ticket = "pass"; // missing
    // final String text   = "whatever"; // missing

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.PUT_MESSAGE.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (0)
    };

    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("missing ticket and text not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);

      // not specified whether exception reports one or the other or both
      assertPX(expected, null);
    }
  }


  @Test public void parsePutMessage_TxtTicTxt()
    throws ProtocolException
  {
    final String ticket = "pass";
    final String text   = "whatever";

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.PUT_MESSAGE.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (12 + ticket.length() + text.length() + text.length()),

      MsgBoardType.TEXT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) text.length(),
      (byte)'w', (byte)'h', (byte)'a', (byte)'t',
      (byte)'e', (byte)'v', (byte)'e', (byte)'r',

      MsgBoardType.TICKET.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) ticket.length(),
      (byte)'p', (byte)'a', (byte)'s', (byte)'s',

      MsgBoardType.TEXT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) text.length(),
      (byte)'w', (byte)'h', (byte)'a', (byte)'t',
      (byte)'e', (byte)'v', (byte)'e', (byte)'r'
    };


    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("duplicate text not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.TEXT);
    }
  }


  @Test public void parsePutMessage_TxtTicO()
    throws ProtocolException
  {
    final String ticket = "pass";
    final String text   = "whatever";
    final String orig   = "me"; // doesn't belong here

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.PUT_MESSAGE.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (12 + ticket.length() + text.length() + orig.length()),

      MsgBoardType.TEXT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) text.length(),
      (byte)'w', (byte)'h', (byte)'a', (byte)'t',
      (byte)'e', (byte)'v', (byte)'e', (byte)'r',

      MsgBoardType.TICKET.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) ticket.length(),
      (byte)'p', (byte)'a', (byte)'s', (byte)'s',

      MsgBoardType.ORIGINATOR.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) orig.length(),
      (byte)'m', (byte)'e'
    };


    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("unexpected originator not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.ORIGINATOR);
    }
  }


  @Test public void parsePutMessage_TxtTic_excessive()
    throws ProtocolException
  {
    final String ticket = "pass";
    final String text   = "whatever";

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.PUT_MESSAGE.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (8 + ticket.length() + text.length()),

      MsgBoardType.TEXT.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0,
      (byte) (text.length() + 200), // excessive, points beyond end
      (byte)'w', (byte)'h', (byte)'a', (byte)'t',
      (byte)'e', (byte)'v', (byte)'e', (byte)'r',

      MsgBoardType.TICKET.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) ticket.length(),
      (byte)'p', (byte)'a', (byte)'s', (byte)'s'
    };


    try {
      MsgBoardRequest mbr = rp.parse(data, 0, data.length);
      fail("excessive nested TLV not detected: "+mbr);
    } catch (Exception expected) {
      // expected.printStackTrace(System.err);
      assertPX(expected, MsgBoardType.TEXT);
    }
  }



  @Test public void parseObtainTicket_O()
    throws ProtocolException
  {
    final String username = "me";

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.OBTAIN_TICKET.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (4 + username.length()),

      MsgBoardType.ORIGINATOR.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) username.length(),
      (byte)'m', (byte)'e'
    };

    MsgBoardRequest mbr = rp.parse(data, 0, data.length+1);

    assertNotNull("no result", mbr);
    assertEquals("wrong type", ReqType.OBTAIN_TICKET, mbr.getReqType());
    assertEquals("wrong originator", username, mbr.getOriginator());

    assertNull("unexpected limit",      mbr.getLimit());
    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected ticket",     mbr.getTicket());
    assertNull("unexpected text",       mbr.getText());
  }


  @Test public void parseReturnTicket_Tic()
    throws ProtocolException
  {
    final String ticket = "pass";

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.RETURN_TICKET.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (4 + ticket.length()),

      MsgBoardType.TICKET.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) ticket.length(),
      (byte)'p', (byte)'a', (byte)'s', (byte)'s'
    };

    MsgBoardRequest mbr = rp.parse(data, 0, data.length+1);

    assertNotNull("no result", mbr);
    assertEquals("wrong type", ReqType.RETURN_TICKET, mbr.getReqType());
    assertEquals("wrong ticket", ticket, mbr.getTicket());

    assertNull("unexpected limit",      mbr.getLimit());
    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected originator", mbr.getOriginator());
    assertNull("unexpected text",       mbr.getText());
  }


  @Test public void parseReplaceTicket_Tic()
    throws ProtocolException
  {
    final String ticket = "pass";

    RequestParser rp = new TLVRequestParserImpl();
    byte[] data = new byte[]{
      MsgBoardType.REPLACE_TICKET.typeByte, MsgBoardTLV.LENGTH_OF_LENGTH_2,
      (byte) 0, (byte) (4 + ticket.length()),

      MsgBoardType.TICKET.typeByte,
      MsgBoardTLV.LENGTH_OF_LENGTH_2, (byte) 0, (byte) ticket.length(),
      (byte)'p', (byte)'a', (byte)'s', (byte)'s'
    };

    MsgBoardRequest mbr = rp.parse(data, 0, data.length+1);

    assertNotNull("no result", mbr);
    assertEquals("wrong type", ReqType.REPLACE_TICKET, mbr.getReqType());
    assertEquals("wrong ticket", ticket, mbr.getTicket());

    assertNull("unexpected limit",      mbr.getLimit());
    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected originator", mbr.getOriginator());
    assertNull("unexpected text",       mbr.getText());
  }


  //@@@ There's one set of negative tests for PUT_MESSAGE:
  //@@@     missing TLV, duplicate TLV, unexpected TLV, overlong TLV
  //@@@ Add negative tests for other request types relying on parseGeneric?
  //@@@ Full set, just missing TLV, something inbetween?

  //@@@ is it possible to generate string decoding error with UTF-8 encoding?
  //@@@ otherwise impossible to test for Catalog.INVALID_TLV_STRING_ENC_3

}
