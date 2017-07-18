/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import pityoulish.mbserver.ProblemFactory;
import pityoulish.mbserver.SanityCheckerBase;


/**
 * Default implementation of {@link MSanityChecker}.
 */
public class DefaultMSanityChecker<P> extends SanityCheckerBase<P>
  implements MSanityChecker<P>
{
  /**
   * Creates a new sanity checker.
   *
   * @param pf   the problem factory to use
   */
  protected DefaultMSanityChecker(ProblemFactory<P> pf)
  {
    super(pf);
  }


  // non-javadoc, see interface
  public P checkMarker(String marker)
  {
    if (marker == null)
       return null; // null marker is valid

    //if (!boardSequencer.isSane(marker))
    //@@@ define in Catalog:   "invalid marker '"+marker+"'"

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

    return null;
  }

}
