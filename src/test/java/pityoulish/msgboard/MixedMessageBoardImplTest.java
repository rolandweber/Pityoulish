/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import org.junit.*;
import static org.junit.Assert.*;


public class MixedMessageBoardImplTest
{
  @Test public void constructor_OK()
  {
    MixedMessageBoardImpl board = new MixedMessageBoardImpl(8);
  }


  @Test public void constructor_args()
  {
    try {
      MixedMessageBoardImpl board = new MixedMessageBoardImpl(0);
      fail("capacity 0 not detected");
    } catch (Exception expected) {
    }

    try {
      MixedMessageBoardImpl board = new MixedMessageBoardImpl(-8);
      fail("negative capacity not detected");
    } catch (Exception expected) {
    }
  }


  @Test public void putMessage_args()
  {
    MixedMessageBoardImpl board = new MixedMessageBoardImpl(3);

    try {
      Message msg = board.putMessage(null, "nonsense");
      fail("missing originator not detected");
    } catch (Exception expected) {
    }

    try {
      Message msg = board.putMessage("originator", null);
      fail("missing text not detected");
    } catch (Exception expected) {
    }

  }


  @Test public void putMessage_OK()
  {
    String originator = "myself";
    String text = "total nonsense";
    MixedMessageBoardImpl board = new MixedMessageBoardImpl(3);

    Message msg = board.putMessage(originator, text);

    assertNotNull("no message", msg);
    assertNotNull("missing timestamp", msg.getTimestamp());
    assertEquals("wrong originator", originator, msg.getOriginator());
    assertEquals("wrong text", text, msg.getText());
  }


  @Test public void putSystemMessage_args()
  {
    MixedMessageBoardImpl board = new MixedMessageBoardImpl(3);

    try {
      Message msg = board.putSystemMessage(null, null);
      fail("missing text not detected");
    } catch (Exception expected) {
    }

    //@@@ check with non-null slot as well? currently not supported
  }


  @Test public void putSystemMessage_NoSlot()
  {
    String slot = null;
    String text = "system info";
    MixedMessageBoardImpl board = new MixedMessageBoardImpl(3);

    Message msg = board.putSystemMessage(slot, text);

    assertNotNull("no message", msg);
    assertNotNull("missing originator", msg.getOriginator());
    assertNotNull("missing timestamp", msg.getTimestamp());
    assertEquals("wrong text", text, msg.getText());
  }


  @Test public void listMessages_args()
  {
    MixedMessageBoardImpl board = new MixedMessageBoardImpl(3);

    try {
      MessageBatch mb = board.listMessages(0, null);
      fail("limit 0 not detected");
    } catch (Exception expected) {
    }

    try {
      MessageBatch mb = board.listMessages(-8, null);
      fail("negative limit not detected");
    } catch (Exception expected) {
    }

    //@@@ check with invalid marker as well? currently not supported
  }


  @Test public void listMessages_emptyBoard()
  {
    MixedMessageBoardImpl board = new MixedMessageBoardImpl(3);

    MessageBatch mb = board.listMessages(8, null);

    assertNotNull("no message batch", mb);
    assertNotNull("no message list", mb.getMessages());
    assertEquals("unexpected messages", 0, mb.getMessages().size());
    assertNotNull("no marker", mb.getMarker());
    assertEquals("discontinuous", false, mb.isDiscontinuous());
  }


  @Test public void listMessages_all()
  {
    final int capacity = 8;
    final int limit = 8;
    String originator = "myself";
    String[] texts = new String[]{
      "nonsense",
      "hogwash",
      "poppycock",
      "tommyrot",
      "bupkis"
    };

    MixedMessageBoardImpl board = new MixedMessageBoardImpl(capacity);
    for (String text: texts)
       board.putMessage(originator, text);

    MessageBatch mb = board.listMessages(limit, null);

    assertNotNull("no message batch", mb);
    assertNotNull("no message list", mb.getMessages());
    assertEquals("wrong number of messages",
                 texts.length, mb.getMessages().size());
    assertNotNull("no marker", mb.getMarker());
    assertEquals("discontinuous", false, mb.isDiscontinuous());

    for (int i=0; i<texts.length; i++)
       assertEquals("wrong message #"+i, texts[i],
                    mb.getMessages().get(i).getText());
  }


  @Test public void listMessages_some()
  {
    final int capacity = 8;
    final int limit = 3;
    String originator = "myself";
    String[] texts = new String[]{
      "nonsense",
      "rubbish",
      "gibberish",
      "balderdash",
      "malarkey"
    };

    MixedMessageBoardImpl board = new MixedMessageBoardImpl(capacity);
    for (String text: texts)
       board.putMessage(originator, text);

    MessageBatch mb = board.listMessages(limit, null);

    assertNotNull("no message batch", mb);
    assertNotNull("no message list", mb.getMessages());
    assertEquals("wrong number of messages",
                 limit, mb.getMessages().size());
    assertNotNull("no marker", mb.getMarker());
    assertEquals("discontinuous", false, mb.isDiscontinuous());

    for (int i=0; i<limit; i++)
       assertEquals("wrong message #"+i, texts[i],
                    mb.getMessages().get(i).getText());
  }


  //@@@ listMessages with marker


  @Test public void testCapacity()
  {
    String originator = "myself";
    String[] texts = new String[]{
      "rigmarole",
      "verbiage",
      "flubdub",
      "folderol"
    };

    MixedMessageBoardImpl board = new MixedMessageBoardImpl(3);
    for (String text: texts)
       board.putMessage(originator, text);

    MessageBatch mb = board.listMessages(8, null);
    assertEquals("unexpected message count", 3, mb.getMessages().size());
    assertEquals("unexpected oldest message",
                 texts[1], mb.getMessages().get(0).getText());
  }


}
