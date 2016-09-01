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

    try {
      MessageBatch mb = board.listMessages(8, "");
      fail("empty marker not detected");
    } catch (Exception expected) {
    }

    try {
      MessageBatch mb = board.listMessages(8, "_:");
      fail("invalid marker not detected");
    } catch (Exception expected) {
    }

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
    String originator = "junit";
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
     {
       assertEquals("wrong message #"+i, texts[i],
                    mb.getMessages().get(i).getText());
       assertEquals("wrong originator #"+i, originator,
                    mb.getMessages().get(i).getOriginator());
     }
  }


  @Test public void listMessages_some()
  {
    final int capacity = 8;
    final int limit = 3;
    String slot = null;
    String[] texts = new String[]{
      "nonsense",
      "rubbish",
      "gibberish",
      "balderdash",
      "malarkey"
    };

    MixedMessageBoardImpl board = new MixedMessageBoardImpl(capacity);
    for (String text: texts)
       board.putSystemMessage(slot, text);

    MessageBatch mb = board.listMessages(limit, null);

    assertNotNull("no message batch", mb);
    assertNotNull("no message list", mb.getMessages());
    assertEquals("wrong number of messages",
                 limit, mb.getMessages().size());
    assertNotNull("no marker", mb.getMarker());
    assertEquals("discontinuous", false, mb.isDiscontinuous());

    for (int i=0; i<limit; i++)
     {
       assertEquals("wrong message #"+i, texts[i],
                    mb.getMessages().get(i).getText());
       assertNotNull("missing originator #"+i,
                     mb.getMessages().get(i).getOriginator());
     }
  }


  @Test public void listMessages_continuous()
  {
    final int capacity = 8;
    final int skip  = 3;
    final int limit = 4;
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

    MessageBatch mb0 = board.listMessages(capacity, null);
    MessageBatch mb1 = board.listMessages(skip, null);
    MessageBatch mb2 = board.listMessages(limit, mb1.getMarker());

    final int expected = texts.length - skip;
    assertNotNull("no message batch", mb2);
    assertNotNull("no message list", mb2.getMessages());
    assertEquals("wrong number of messages",
                 expected, mb2.getMessages().size());
    assertNotNull("no marker", mb2.getMarker());
    assertEquals("discontinuous", false, mb2.isDiscontinuous());

    // mb0 has a marker that points at the end of the messages
    // mb1 has a marker that points within the messages
    assertEquals("wrong marker", mb0.getMarker(), mb2.getMarker());
    assertNotEquals("unexpected marker", mb1.getMarker(), mb2.getMarker());

    for (int i=0; i<expected; i++)
     {
       assertEquals("wrong message #"+i, texts[i + skip],
                    mb2.getMessages().get(i).getText());
       assertEquals("wrong originator #"+i, originator,
                    mb2.getMessages().get(i).getOriginator());
     }
  }


  @Test public void listMessages_no_more()
  {
    final int capacity = 8;
    final int limit = capacity;
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

    // first batch contains all the messages
    // second batch is empty

    MessageBatch mb1 = board.listMessages(limit, null);
    MessageBatch mb2 = board.listMessages(limit, mb1.getMarker());

    final int expected = 0;
    assertNotNull("no message batch", mb2);
    assertNotNull("no message list", mb2.getMessages());
    assertEquals("wrong number of messages",
                 expected, mb2.getMessages().size());
    assertEquals("wrong marker", mb1.getMarker(), mb2.getMarker());
    assertEquals("discontinuous", false, mb2.isDiscontinuous());
  }


  @Test public void testCapacity_userMessages()
  {
    final int capacity = 3;
    String originator = "myself";
    String[] texts = new String[]{
      "rigmarole",
      "verbiage",
      "flubdub",
      "folderol"
    };

    MixedMessageBoardImpl board = new MixedMessageBoardImpl(capacity);
    for (String text: texts)
       board.putMessage(originator, text);

    MessageBatch mb = board.listMessages(8, null);
    assertEquals("unexpected message count",
                 capacity, mb.getMessages().size());
    assertEquals("discontinuous", false, mb.isDiscontinuous());

    for (int i=0; i<capacity; i++)
       assertEquals("wrong message #"+i, texts[i + texts.length - capacity],
                    mb.getMessages().get(i).getText());
  }

  //@@@ capacity with system messages
  //@@@ capacity with mixed user and system messages


  @Test public void listMessages_droppedUserMessage()
  {
    final int capacity = 3;
    String originator = "rumour";
    String[] texts = new String[]{ // need more than twice the capacity!
      "nonsense",
      "rigmarole",
      "verbiage",
      "gibberish",
      "balderdash",
      "flubdub",
      "folderol",
      "malarkey"
    };

    MixedMessageBoardImpl board = new MixedMessageBoardImpl(capacity);
    for (int i=0; i<capacity; i++)
       board.putMessage(originator, texts[i]);

    MessageBatch mb1 = board.listMessages(capacity, null);
    for (int i=capacity; i<texts.length; i++)
       board.putMessage(originator, texts[i]);
    // some unread user message is dropped by now

    MessageBatch mb2 = board.listMessages(capacity+1, mb1.getMarker());

    assertNotNull("no message batch", mb2);
    assertNotNull("no message list", mb2.getMessages());
    assertEquals("wrong number of messages",
                 capacity, mb2.getMessages().size());
    assertNotNull("no marker", mb2.getMarker());
    assertEquals("wrong discontinuous", true, mb2.isDiscontinuous());

    for (int i=0; i<capacity; i++)
     {
       assertEquals("wrong message #"+i, texts[i + texts.length - capacity],
                    mb2.getMessages().get(i).getText());
       assertEquals("wrong originator #"+i, originator,
                    mb2.getMessages().get(i).getOriginator());
     }
  }


  @Test public void listMessages_droppedSystemMessage()
  {
    final int capacity = 3;
    String    originator = "rumour";
    String    slot = null;
    String[]  texts = new String[]{ // need more than twice the capacity!
      "nonsense",
      "rigmarole",
      "verbiage",
      "gibberish",
      "balderdash",
      "flubdub",
      "folderol",
      "malarkey"
    };

    MixedMessageBoardImpl board = new MixedMessageBoardImpl(capacity);
    for (int i=0; i<capacity; i++)
       board.putMessage(originator, texts[i]);

    MessageBatch mb1 = board.listMessages(capacity, null);
    for (int i=capacity; i<texts.length; i++)
       board.putSystemMessage(slot, texts[i]);
    // some unread system message is dropped by now

    MessageBatch mb2 = board.listMessages(capacity+1, mb1.getMarker());

    assertNotNull("no message batch", mb2);
    assertNotNull("no message list", mb2.getMessages());
    assertEquals("wrong number of messages",
                 capacity, mb2.getMessages().size());
    assertNotNull("no marker", mb2.getMarker());
    assertEquals("discontinuous", false, mb2.isDiscontinuous());

    for (int i=0; i<capacity; i++)
     {
       assertEquals("wrong message #"+i, texts[i + texts.length - capacity],
                    mb2.getMessages().get(i).getText());
       assertNotNull("missing originator #"+i,
                    mb2.getMessages().get(i).getOriginator());
     }
  }

}
