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
  /** The sequencer for checking markers. */
  protected final Sequencer boardSequencer;

  /**
   * The pattern for valid characters in texts.
   * The length requirements are not encoded in the pattern.
   * By checking length explicitly, we can provide better problem reports.
   */
  public final static
    Pattern DEFAULT_TEXT_PATTERN = Pattern.compile("(?U)[\\p{Print}\\s]+");

  protected final int textMaxLength;

  protected final Pattern textPattern;

  /**
   * The pattern for valid characters in originators.
   * The length requirements for originators are not encoded in the pattern.
   * By checking length explicitly, we can provide better problem reports.
   * <br>
   * This pattern intentionally allows characters that the ticket manager
   * would not accept in a username. This allows for more error scenarios.
   * Also, originators of system messages are not valid usernames.
   */
  public final static
    Pattern DEFAULT_ORIGINATOR_PATTERN = Pattern.compile("[A-Za-z0-9.@-]+");

  protected final int originatorMaxLength;

  protected final Pattern originatorPattern;



  /**
   * Creates a new sanity checker.
   *
   * @param pf         the problem factory to use
   * @param seq        the sequencer for checking markers, or
   *                   <code>null</code> to allow all markers
   * @param tmaxlen    the maximum length of a originator
   * @param tpattern   the regex checking for valid characters in texts,
   *                   <code>null</code> for the
   *                   {@link #DEFAULT_TEXT_PATTERN}
   * @param omaxlen    the maximum length of an originator
   * @param opattern   the regex checking for valid characters in originators,
   *                   <code>null</code> for the
   *                   {@link #DEFAULT_ORIGINATOR_PATTERN}
   */
  protected DefaultMSanityChecker(ProblemFactory<P> pf, Sequencer seq,
                                  int tmaxlen, Pattern tpattern,
                                  int omaxlen, Pattern opattern)
  {
    super(pf);

    if (omaxlen < 1)
       throw new IllegalArgumentException("omaxlen");
    if (tmaxlen < 1)
       throw new IllegalArgumentException("tmaxlen");

    boardSequencer = seq;

    textMaxLength = tmaxlen;
    textPattern =
      (tpattern != null) ? tpattern : DEFAULT_TEXT_PATTERN;

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
    this(pf, seq, 170, null, 16, null);
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

    if (text.length() < 1)
       return problemFactory.newProblem(Catalog.TEXT_EMPTY);

    if (text.length() > textMaxLength)
       return problemFactory.newProblem(Catalog.TEXT_TOO_LONG_1,
                                        String.valueOf(textMaxLength));

    if (!textPattern.matcher(text).matches())
       return problemFactory.newProblem(Catalog.TEXT_BAD_CHARACTER);

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

    if (originator.length() > originatorMaxLength)
       return problemFactory.newProblem(Catalog.ORIGINATOR_TOO_LONG_1,
                                        String.valueOf(originatorMaxLength));

    if (!originatorPattern.matcher(originator).matches())
       return problemFactory.newProblem(Catalog.ORIGINATOR_BAD_CHARACTER);

    return null;
  }

}
