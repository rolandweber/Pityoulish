/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.mbserver;


import pityoulish.i18n.TextEntry;


/**
 * A factory for validation problem indicators, for use with sanity checkers.
 * See {@link SanityCheckerBase} and derived classes.
 */
public interface ProblemFactory<P>
{
  /**
   * Creates a new problem indicator.
   *
   * @param te       a text entry that describes the validation problem
   * @param params   the parameters to be formatted by the text entry
   *
   * @return   a problem indicator, typically including a description
   *           generated from the arguments
   */
  public P newProblem(TextEntry te, Object... params)
    ;

}
