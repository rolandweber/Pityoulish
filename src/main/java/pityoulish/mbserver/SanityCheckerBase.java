/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.mbserver;



/**
 * A base class for sanity checkers.
 * It keeps a reference to the {@link ProblemFactory} to be used.
 * This class is thread-safe if the factory and other helper classes are.
 * Actual sanity checks must be defined and implemented in derived classes.
 */
public abstract class SanityCheckerBase<P>
{
  protected final ProblemFactory<P> problemFactory;

  /**
   * Creates a new sanity checker.
   *
   * @param pf   the problem factory to use
   */
  protected SanityCheckerBase(ProblemFactory<P> pf)
  {
    if (pf == null)
       throw new NullPointerException("ProblemFactory");

    problemFactory = pf;
  }

}

