/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import org.junit.*;
import static org.junit.Assert.*;

import pityoulish.mbserver.StringProblemFactory;


public class DefaultMSanityCheckerTest
{
  public final static DefaultMSanityChecker<String> newChecker()
  {
    return new DefaultMSanityChecker<>(new StringProblemFactory(),
                                       new SimpleSequencerImpl(),
                                       35, null, 16, null);
    // max length of text (35) and originator (16)
  }


  @Test public void create_badArgs()
    throws Exception
  {
    final StringProblemFactory spf = new StringProblemFactory();
    final Sequencer seq = new SimpleSequencerImpl();
    final int tmaxlen = 666;
    final int omaxlen = 62;

    try {
      DefaultMSanityChecker<String> checker =
        new DefaultMSanityChecker<>(null, seq);
      fail("missing problem factory not detected (2 args): "+checker);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      DefaultMSanityChecker<String> checker =
        new DefaultMSanityChecker<>(null, seq, tmaxlen, null, omaxlen, null);
      fail("missing problem factory not detected (5 args): "+checker);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      DefaultMSanityChecker<String> checker =
        new DefaultMSanityChecker<>(spf, seq, -1, null, omaxlen, null);
      fail("invalid maximum text length not detected: "+checker);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      DefaultMSanityChecker<String> checker =
        new DefaultMSanityChecker<>(spf, seq, tmaxlen, null, -1, null);
      fail("invalid maximum originator length not detected: "+checker);
    } catch (RuntimeException expected) {
      // expected
    }
  }


  @Test public void checkMarker_null()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();

    //@@@ TBD: exception or problem report when called with null? #11
    try {
      String problem = checker.checkMarker(null);
      fail("missing marker not detected");
    } catch (RuntimeException expected) {
      // expected
    }
  }

  @Test public void checkMarker_empty()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String problem = checker.checkMarker("");
    assertNotNull("missing marker not detected", problem);
  }

  @Test public void checkMarker_invalid()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String problem = checker.checkMarker(".a");
    assertNotNull("invalid marker not detected", problem);
  }

  @Test public void checkMarker_OK()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String marker = checker.boardSequencer.createMessageID();

    String problem = checker.checkMarker(marker);
    assertNull("valid marker rejected", problem);
  }



  @Test public void checkText_null()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();

    //@@@ TBD: exception or problem report when called with null? #11
    try {
      String problem = checker.checkText(null);
      fail("missing text not detected");
    } catch (RuntimeException expected) {
      // expected
    }
  }

  @Test public void checkText_empty()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String problem = checker.checkText("");
    assertNotNull("missing text not detected", problem);
  }

  @Test public void checkText_long()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String text = "Please stop talking, you are droning on!"; // exceeds 35

    String problem = checker.checkText(text);
    assertNotNull("long text not detected", problem);
  }

  @Test public void checkText_invalid()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String problem = checker.checkText("abc \u001f xyz");
    assertNotNull("invalid text not detected", problem);
  }

  @Test public void checkText_OK()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String text = "Punctuation! 0-9\r\n \tUmlaut \u00e4!";

    String problem = checker.checkText(text);
    assertNull("valid text rejected", problem);
  }


  @Test public void checkOriginator_null()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();

    //@@@ TBD: exception or problem report when called with null? #11
    try {
      String problem = checker.checkOriginator(null);
      fail("missing originator not detected");
    } catch (RuntimeException expected) {
      // expected
    }
  }

  @Test public void checkOriginator_empty()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String problem = checker.checkOriginator("");
    assertNotNull("missing originator not detected", problem);
  }

  @Test public void checkOriginator_long()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String problem = checker.checkOriginator("ThisIsAnInsanelyLongOriginator");
    assertNotNull("long originator not detected", problem);
  }

  @Test public void checkOriginator_invalid()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String problem = checker.checkOriginator("user name");
    assertNotNull("whitespace in originator not detected", problem);
  }

  @Test public void checkOriginator_OK()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    String problem = checker.checkOriginator("somebody");
    assertNull("valid originator rejected", problem);
  }

  @Test public void checkOriginator_rw()
    throws Exception
  {
    DefaultMSanityChecker<String> checker = newChecker();
    // this one is explicitly allowed, for convenience when testing manually...
    String problem = checker.checkOriginator("rw");
    assertNull("originator 'rw' not allowed", problem);
  }

}
