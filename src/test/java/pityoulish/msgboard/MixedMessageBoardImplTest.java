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


  @Test public void testCapacity()
  {
    String originator = "myself";
    MixedMessageBoardImpl board = new MixedMessageBoardImpl(3);

    board.putMessage(originator, "nonsense");
    board.putMessage(originator, "more nonsense");
    board.putMessage(originator, "even more nonsense");
    board.putMessage(originator, "bupkis");

    //@@@ get batch, verify that only the last three are available
    //@@@ currently not implemented
  }


}
