/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import java.util.Comparator;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Implements tests for the must-have requirements of a Sequencer.
 * To run the tests against a specific implementation, create a subclass.
 */
public abstract class SequencerMustHave
{
  /** The maximum length of a message ID to be tolerated. */
  public final static int MSGID_MAX_LENGTH = 17;


  /**
   * Creates a sequencer to test.
   *
   * @return an instance of the class to be tested
   */
  public abstract Sequencer newTestSubject()
    ;


  /**
   * Checks some basic properties of a message ID.
   *
   * @param msgid       the message ID
   */
  public static void _checkMessageID(String msgid)
  {
    assertNotNull("no message ID", msgid);
    assertTrue("message ID too long", msgid.length() <= MSGID_MAX_LENGTH);

    //@@@ check for URL-safety with a regular expression
  }


  /**
   * Create a message ID and check some basic properties.
   * Calls {@link #_checkMessageID} before returning the ID.
   *
   * @param seq   the sequencer to use
   * @return      the generated and checked message ID
   */
  public static String _createCheckedMessageID(Sequencer seq)
  {
    String msgid = seq.createMessageID();
    _checkMessageID(msgid);
    return msgid;
  }


  @Test public void createMessageID()
    throws Exception
  {
    Sequencer seq = newTestSubject();

    String msgid = seq.createMessageID();
    _checkMessageID(msgid);
  }


  @Test public void isSane()
    throws Exception
  {
    Sequencer seq = newTestSubject();

    assertFalse("null", seq.isSane(null));
    assertFalse("empty", seq.isSane(""));
    assertFalse("URL-unsafe", seq.isSane("/&"));

    String msgid = _createCheckedMessageID(seq);
    assertTrue("generated ID", seq.isSane(msgid));
  }


  @Test public void getComparator()
    throws Exception
  {
    Sequencer seq = newTestSubject();

    Comparator<String> comp = seq.getComparator();
    assertNotNull("no comparator", comp);
  }


  @Test public void getComparator_compare()
    throws Exception
  {
    Sequencer seq = newTestSubject();

    Comparator<String> comp = seq.getComparator();
    assertNotNull("no comparator", comp);

    String msgid1 = _createCheckedMessageID(seq);
    String msgid2 = _createCheckedMessageID(seq);

    assertTrue("msgid1 === msgid1", comp.compare(msgid1, msgid1) == 0);
    assertTrue("msgid1  <  msgid2", comp.compare(msgid1, msgid2) <  0);
    assertTrue("msgid2  >  msgid1", comp.compare(msgid2, msgid1) >  0);
    assertTrue("msgid2 === msgid2", comp.compare(msgid2, msgid2) == 0);
  }

}
