/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.sockets.server.MsgBoardRequest.ReqType;

import org.junit.*;
import static org.junit.Assert.*;

public class MsgBoardRequestImplTest
{

  @Test public void construct_all()
  {
    final ReqType rtype = ReqType.RETURN_TICKET;
    final Integer limit  = new Integer(8);
    final String  marker  = "Denmark";
    final String  ticket   = "poison";
    final String  text      = "The rest is silence.";
    final String  originator = "Hamlet";

    MsgBoardRequest mbr = new MsgBoardRequestImpl
      (rtype, limit, marker, ticket, text, originator);

    assertNotNull("no object", mbr);
    assertEquals("wrong type",       rtype,      mbr.getReqType());
    assertEquals("wrong limit",      limit,      mbr.getLimit());
    assertEquals("wrong marker",     marker,     mbr.getMarker());
    assertEquals("wrong ticket",     ticket,     mbr.getTicket());
    assertEquals("wrong text",       text,       mbr.getText());
    assertEquals("wrong originator", originator, mbr.getOriginator());
  }


  @Test public void construct_min()
  {
    final ReqType rtype = ReqType.LIST_MESSAGES;
    final Integer limit  = null;
    final String  marker  = null;
    final String  ticket   = null;
    final String  text      = null;
    final String  originator = null;

    MsgBoardRequest mbr = new MsgBoardRequestImpl
      (rtype, limit, marker, ticket, text, originator);

    assertNotNull("no object", mbr);
    assertEquals("wrong type",       rtype,      mbr.getReqType());
    assertEquals("wrong limit",      limit,      mbr.getLimit());
    assertEquals("wrong marker",     marker,     mbr.getMarker());
    assertEquals("wrong ticket",     ticket,     mbr.getTicket());
    assertEquals("wrong text",       text,       mbr.getText());
    assertEquals("wrong originator", originator, mbr.getOriginator());
  }


  @Test public void construct_bad()
  {
    final ReqType rtype = null; // required
    final Integer limit  = null;
    final String  marker  = null;
    final String  ticket   = null;
    final String  text      = null;
    final String  originator = null;

    try {
      MsgBoardRequest mbr = new MsgBoardRequestImpl
        (rtype, limit, marker, ticket, text, originator);
      fail("missing type not detected: "+mbr);
    } catch (RuntimeException expected) {
      // expected
    }
  }



  @Test public void newListMessages()
  {
    final Integer limit  = new Integer(35);
    final String  marker = "wherever";

    MsgBoardRequest mbr =
      MsgBoardRequestImpl.newListMessages(limit, marker);

    assertNotNull("no object", mbr);
    assertEquals("wrong type", ReqType.LIST_MESSAGES, mbr.getReqType());
    assertEquals("wrong limit",  limit,  mbr.getLimit());
    assertEquals("wrong marker", marker, mbr.getMarker());

    assertNull("unexpected ticket",     mbr.getTicket());
    assertNull("unexpected text",       mbr.getText());
    assertNull("unexpected originator", mbr.getOriginator());
  }


  @Test public void newPutMessage()
  {
    final String ticket = "open_sesame";
    final String text   = "Cookies!";

    MsgBoardRequest mbr =
      MsgBoardRequestImpl.newPutMessage(ticket, text);

    assertNotNull("no object", mbr);
    assertEquals("wrong type", ReqType.PUT_MESSAGE, mbr.getReqType());
    assertEquals("wrong ticket", ticket, mbr.getTicket());
    assertEquals("wrong text",   text,   mbr.getText());

    assertNull("unexpected limit",      mbr.getLimit());
    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected originator", mbr.getOriginator());
  }


  @Test public void newObtainTicket()
  {
    final String originator = "nobody";

    MsgBoardRequest mbr =
      MsgBoardRequestImpl.newObtainTicket(originator);

    assertNotNull("no object", mbr);
    assertEquals("wrong type", ReqType.OBTAIN_TICKET, mbr.getReqType());
    assertEquals("wrong originator", originator, mbr.getOriginator());

    assertNull("unexpected limit",      mbr.getLimit());
    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected ticket",     mbr.getTicket());
    assertNull("unexpected text",       mbr.getText());
  }


  @Test public void newReturnTicket()
  {
    final String ticket = "open_sesame";

    MsgBoardRequest mbr =
      MsgBoardRequestImpl.newReturnTicket(ticket);

    assertNotNull("no object", mbr);
    assertEquals("wrong type", ReqType.RETURN_TICKET, mbr.getReqType());
    assertEquals("wrong ticket", ticket, mbr.getTicket());

    assertNull("unexpected limit",      mbr.getLimit());
    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected text",       mbr.getText());
    assertNull("unexpected originator", mbr.getOriginator());
  }


  @Test public void newReplaceTicket()
  {
    final String ticket = "open_sesame";

    MsgBoardRequest mbr =
      MsgBoardRequestImpl.newReplaceTicket(ticket);

    assertNotNull("no object", mbr);
    assertEquals("wrong type", ReqType.REPLACE_TICKET, mbr.getReqType());
    assertEquals("wrong ticket", ticket, mbr.getTicket());

    assertNull("unexpected limit",      mbr.getLimit());
    assertNull("unexpected marker",     mbr.getMarker());
    assertNull("unexpected text",       mbr.getText());
    assertNull("unexpected originator", mbr.getOriginator());
  }



  @Test public void to_String()
  {
    final ReqType rtype = ReqType.RETURN_TICKET;
    final Integer limit  = new Integer(666);
    final String  marker  = "Denmark";
    final String  ticket   = "poison";
    final String  text      = "The rest is silence.";
    final String  originator = "Hamlet";

    MsgBoardRequest mbr = new MsgBoardRequestImpl
      (rtype, limit, marker, ticket, text, originator);
    final String debug = mbr.toString();

    assertTrue("missing class info",
               debug.indexOf("MsgBoardRequest") >= 0);
    assertTrue("missing request type",
               debug.indexOf(rtype.toString()) >= 0);
    assertTrue("missing limit",
               debug.indexOf(limit.toString()) >= 0);
    assertTrue("missing marker",
               debug.indexOf(marker) >= 0);
    assertTrue("missing ticket",
               debug.indexOf(ticket) >= 0);
    assertTrue("missing text",
               debug.indexOf(text) >= 0);
    assertTrue("missing originator",
               debug.indexOf(originator) >= 0);
  }


}
