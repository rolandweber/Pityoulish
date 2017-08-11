/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import java.util.regex.Pattern;

import pityoulish.mbserver.ProblemFactory;
import pityoulish.mbserver.SanityCheckerBase;


/**
 * Default implementation of {@link MSanityChecker}.
 */
public class DefaultMSanityChecker<P> extends SanityCheckerBase<P>
  implements MSanityChecker<P>
{
  /**
   * The pattern for valid characters in originators.
   * The length requirements for originators are not encoded in the pattern.
   * By checking length explicitly, we can provide better problem reports.
   * <br/>
   * This pattern intentionally allows characters that the ticket manager
   * would not accept in a username. This allows for more error scenarios.
   * Also, originators of system messages are not valid usernames.
   */
  public final static
    Pattern DEFAULT_ORIGINATOR_PATTERN = Pattern.compile("[A-Za-z0-9_.@-]+");

  protected final int originatorMinLength;

  protected final int originatorMaxLength;

  protected final Pattern originatorPattern;


  /** The sequencer for checking markers. */
  protected final Sequencer boardSequencer;


  /**
   * Creates a new sanity checker.
   *
   * @param pf         the problem factory to use
   * @param seq        the sequencer for checking markers, or
   *                   <code>null</code> to allow all markers
   * @param ominlen    the minimum length of a originator
   * @param omaxlen    the maximum length of a originator
   * @param opattern   the regex checking for valid characters in originators,
   *                   <code>null</code> for the
   *                   {@link #DEFAULT_ORIGINATOR_PATTERN}
   */
  protected DefaultMSanityChecker(ProblemFactory<P> pf, Sequencer seq,
                                  int ominlen, int omaxlen, Pattern opattern)
  {
    super(pf);

    if (ominlen < 1)
       throw new IllegalArgumentException("ominlen");
    if (omaxlen < 1)
       throw new IllegalArgumentException("omaxlen");
    if (omaxlen < ominlen)
       throw new IllegalArgumentException("omaxlen < ominlen");

    boardSequencer = seq;
    originatorMinLength = ominlen;
    originatorMaxLength = omaxlen;
    originatorPattern =
      (opattern != null) ? opattern : DEFAULT_ORIGINATOR_PATTERN;
  }


  /**
   * Creates a new sanity checker, with default restrictions.
   *
   * @param pf   the problem factory to use
   * @param seq  the sequencer for checking markers
   */
  protected DefaultMSanityChecker(ProblemFactory<P> pf,
                                  Sequencer seq)
  {
    this(pf, seq, 1, 16, null);
  }


  // non-javadoc, see interface
  public P checkMarker(String marker)
  {
    if (marker == null)
       throw new NullPointerException("marker");

    if ((boardSequencer != null) && !boardSequencer.isSane(marker))
       return problemFactory.newProblem(Catalog.INVALID_MARKER);

    return null;
  }


  // non-javadoc, see interface
  public P checkText(String text)
  {
    if (text == null)
       throw new NullPointerException("text");

    //@@@ check text for length and valid characters
    //@@@ max length and/or pattern as constructor arguments?
    //@@@ define problem messages in Catalog

    return null;
  }


  // non-javadoc, see interface
  public P checkOriginator(String originator)
  {
    if (originator == null)
       throw new NullPointerException("originator");

    // for convenience during manual tests, this one is always allowed...
    if ("rw".equals(originator))
       return null;

    if (originator.length() < 1)
       return problemFactory.newProblem(Catalog.ORIGINATOR_EMPTY);

    if (originator.length() < originatorMinLength)
       return problemFactory.newProblem(Catalog.ORIGINATOR_TOO_SHORT_1,
                                        String.valueOf(originatorMinLength));

    if (originator.length() > originatorMaxLength)
       return problemFactory.newProblem(Catalog.ORIGINATOR_TOO_LONG_1,
                                        String.valueOf(originatorMaxLength));

    if (!originatorPattern.matcher(originator).matches())
       return problemFactory.newProblem(Catalog.ORIGINATOR_BAD_CHARACTER);

    return null;
  }

}
