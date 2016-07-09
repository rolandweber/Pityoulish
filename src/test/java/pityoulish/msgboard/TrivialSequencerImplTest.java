/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import org.junit.*;
import static org.junit.Assert.*;


public class TrivialSequencerImplTest extends SequencerMustHave
{
  public TrivialSequencerImpl newTestSubject()
  {
    return new TrivialSequencerImpl();
  }
}
