/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.i18n;


/**
 * A catalog entry that references a text.
 */
public interface TextEntry extends TextRef
{
  /**
   * Looks up the text and formats parameters with it.
   * <code>this</code> must be an entry for formatting,
   *  with non-negative parameter count.
   * See <code>java.text.MessageFormat</code> for formatting rules.
   * <br>
   * In case of a lookup problem, some fallback formatting is applied.
   * An exception is only thrown if there is a problem with formatting
   * the parameters.
   *
   * @param params      appropriate number of parameters to format
   *
   * @return the formatted text
   *
   * @throws RuntimeException
   *    If the number of parameters is incorrect or
   *    the parameters cannot be formatted with the text.
   *    The latter may be caused by invalid parameters
   *    or by an invalid pattern in the looked-up text.
   */
  public String format(Object... params)
    throws RuntimeException
    ;


  /**
   * Looks up the text and returns it.
   * <br>
   * In case of a lookup problem, some fallback text is returned.
   *
   * @return the looked-up text
   */
  public String lookup()
    ;

}

