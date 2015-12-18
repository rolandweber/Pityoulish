/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.i18n;


/**
 * Reference to a localizable text in a resource bundle.
 */
public interface TextRef extends BundleRef
{
  /**
   * Obtains the lookup key of the text.
   *
   * @return    the key by which to look up the text,
   *            not <code>null</code>
   */
  public String getKey()
    ;


  /**
   * Obtains the number of parameters to be formatted with the text.
   *
   * @return    the number of parameters, or
   *            negative if the text is not a pattern for formatting
   */
  public int getParamCount()
    ;

}

