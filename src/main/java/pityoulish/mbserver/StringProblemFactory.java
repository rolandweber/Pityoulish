/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.mbserver;


import pityoulish.i18n.TextEntry;


/**
 * A {@link ProblemFactory} that creates strings.
 */
public class StringProblemFactory implements ProblemFactory<String>
{
  // non-javadoc, see interface
  public String newProblem(TextEntry te, Object... params)
  {
    return te2string(te, params);
  }


  /**
   * Formats a text entry with optional parameters into a string.
   * This method might be useful for other, non-string problem factories too.
   *
   * @param te       a text entry that describes the validation problem
   * @param params   the parameters to be formatted by the text entry
   *
   * @return   a string generated from the arguments
   */
  public static String te2string(TextEntry te, Object... params)
  {
    if (te == null)
       throw new NullPointerException("TextEntry");

    String result = null;

    if (te.getParamCount() < 0)
     {
       // plain text lookup, no parameters allowed
       if (params.length > 0)
          throw new IllegalArgumentException("params");
       result = te.lookup();
     }
    else
     {
       // parameter count is checked by the formatting method
       result = te.format(params);
     }

    return result;
  }

}
