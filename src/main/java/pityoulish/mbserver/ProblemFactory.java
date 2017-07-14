/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.mbserver;


import pityoulish.i18n.TextEntry;


/**
 * A factory for validation problems,
 * for use by {@link InputValidator} implementations.
 */
public interface ProblemFactory<P>
{
  /**
   * Creates a new problem report.
   *
   * @param te       a text entry that describes the validation problem
   * @param params   the parameters to be formatted by the text entry
   *
   * @return   a problem report generated from the arguments
   */
  public P newProblem(TextEntry te, Object... params)
    ;

}
