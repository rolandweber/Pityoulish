/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

import java.util.regex.Pattern;

import pityoulish.mbserver.ProblemFactory;
import pityoulish.mbserver.SanityCheckerBase;


/**
 * Default implementation of {@link TSanityChecker}.
 */
public class DefaultTSanityChecker<P> extends SanityCheckerBase<P>
  implements TSanityChecker<P>
{
  /**
   * The pattern for valid characters in usernames.
   * The length requirements for the username are not encoded in the pattern.
   * By checking length explicitly, we can provide better problem reports.
   */
  public final static
    Pattern DEFAULT_USERNAME_PATTERN = Pattern.compile("[A-Za-z0-9]+");


  protected final int usernameMinLength;

  protected final int usernameMaxLength;

  protected final Pattern usernamePattern;


  /**
   * Creates a new sanity checker.
   *
   * @param pf         the problem factory to use
   * @param uminlen    the minimum length of a username
   * @param umaxlen    the maximum length of a username
   * @param upattern   the regex checking for valid characters in usernames,
   *                   <code>null</code> for the
   *                   {@link #DEFAULT_USERNAME_PATTERN}
   */
  protected DefaultTSanityChecker(ProblemFactory<P> pf,
                                  int uminlen, int umaxlen, Pattern upattern)
  {
    super(pf);

    if (uminlen < 1)
       throw new IllegalArgumentException("uminlen");
    if (umaxlen < 1)
       throw new IllegalArgumentException("umaxlen");
    if (umaxlen < uminlen)
       throw new IllegalArgumentException("umaxlen < uminlen");

    usernameMinLength = uminlen;
    usernameMaxLength = umaxlen;
    usernamePattern = (upattern != null) ? upattern : DEFAULT_USERNAME_PATTERN;
  }


  /**
   * Creates a new sanity checker, with default restrictions.
   *
   * @param pf   the problem factory to use
   */
  protected DefaultTSanityChecker(ProblemFactory<P> pf)
  {
    this(pf, 6, 12, null);
  }


  // non-javadoc, see interface
  public P checkUsername(String username)
  {
    if (username == null)
       throw new NullPointerException("username");

    // for convenience during manual tests, this one is always allowed...
    if ("rw".equals(username))
       return null;

    if (username.length() < 1)
       return problemFactory.newProblem(Catalog.USERNAME_EMPTY);

    if (username.length() < usernameMinLength)
       return problemFactory.newProblem(Catalog.USERNAME_TOO_SHORT_1,
                                        String.valueOf(usernameMinLength));

    if (username.length() > usernameMaxLength)
       return problemFactory.newProblem(Catalog.USERNAME_TOO_LONG_1,
                                        String.valueOf(usernameMaxLength));

    if (!usernamePattern.matcher(username).matches())
       return problemFactory.newProblem(Catalog.USERNAME_BAD_CHARACTER);

    return null;
  }


  // non-javadoc, see interface
  public P checkToken(String token)
  {
    if (token == null)
       throw new NullPointerException("token");

    if (token.length() < 1)
       return problemFactory.newProblem(Catalog.TOKEN_EMPTY);

    return null;
  }

}
