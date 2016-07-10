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


  @Test public void constructor_bad()
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

}
