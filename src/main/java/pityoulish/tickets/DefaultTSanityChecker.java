/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

import pityoulish.mbserver.ProblemFactory;
import pityoulish.mbserver.SanityCheckerBase;


/**
 * Default implementation of {@link TSanityChecker}.
 */
public class DefaultTSanityChecker<P> extends SanityCheckerBase<P>
  implements TSanityChecker<P>
{
  /**
   * Creates a new sanity checker.
   *
   * @param pf   the problem factory to use
   */
  protected DefaultTSanityChecker(ProblemFactory<P> pf)
  {
    super(pf);
  }


  // non-javadoc, see interface
  public P checkUsername(String username)
  {
    if (username == null)
       throw new NullPointerException("username");

    if (username.length() < 1)
       return problemFactory.newProblem(Catalog.USERNAME_EMPTY);

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
