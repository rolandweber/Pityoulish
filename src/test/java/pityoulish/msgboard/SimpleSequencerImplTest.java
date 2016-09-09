/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import org.junit.*;
import static org.junit.Assert.*;


public class SimpleSequencerImplTest extends SequencerMustHave
{
  public SimpleSequencerImpl newTestSubject()
  {
    return new SimpleSequencerImpl();
  }

  // There's no guarantee that two sequencers have a different instance letter.
  // That makes it tricky to test isSane across instances.
  //@@@ Create test subjects until their first generated ID differs, then
  //@@@ run a set of tests against isSane()? Generalize into a base class.
}
