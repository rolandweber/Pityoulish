/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * Helper class for lookup and formatting in {@link TextEntry}.
 */
public final class CatalogHelper
{
  /* Disable the default constructor. */
  private CatalogHelper()
  {
    throw new UnsupportedOperationException();
  }


  /**
   * Formats parameters with a pattern looked up in the default locale.
   *
   * @param tref        text ref of the pattern to look up.
   *                    This must be a reference for formatting,
   *                    with non-negative parameter count.
   * @param params      appropriate number of parameters to format
   *
   * @return the formatted text, or a fallback if the lookup failed
   *
   * @throws RuntimeException   if something other than the lookup fails
   */
  public static final String format(TextRef tref, Object... params)
    throws RuntimeException
  {
    if (tref == null)
       throw new NullPointerException("TextRef");

    if (tref.getParamCount() < 0)
       throw new IllegalArgumentException("TextRef: "+tref);
    if (params.length != tref.getParamCount())
       throw new IllegalArgumentException("Object...");

    String result = null;
    try {
      ResourceBundle bundle = getBundle(tref);
      String pattern = bundle.getString(tref.getKey());

      // This call parses the pattern. If patterns are used frequently, they
      // should be parsed only once and the resulting MessageFormat cached.
      // That's not necessary for the programming exercises though.
      result = MessageFormat.format(pattern, params);

    } catch (MissingResourceException mre) {
      // the lookup failed, provide a fallback

      StringBuilder sb = new StringBuilder(80);
      sb.append(tref.getBundleName()).append("::").append(tref.getKey());
      for(Object param : params)
         sb.append(" \"").append(param).append("\"");

      result = sb.toString();
    }
    // ClassCastException intentionally not handled here,
    // because TextRef should always refer to a text.
    // IllegalArgumentException intentionally not handled here,
    // because it indicates a bad resource or a mismatch
    // between resource bundle and code.

    return result;
  }


  /**
   * Looks up a text in the default locale.
   *
   * @param tref        the text reference look up
   *
   * @return the text, or a fallback if the lookup failed
   */
  public static final String lookup(TextRef tref)
  {
    if (tref == null)
       throw new NullPointerException("TextRef");

    String result = null;
    try {
      ResourceBundle bundle = getBundle(tref);
      result = bundle.getString(tref.getKey());

    } catch (MissingResourceException mre) {
      // the lookup failed, provide a fallback
      result = tref.getBundleName()+"::"+tref.getKey();
    }
    // ClassCastException intentionally not handled here,
    // because TextRef should always refer to a text

    return result;
  }


  /**
   * Loads a resource bundle, using the default locale.
   *
   * @param bref        the bundle to load
   *
   * @return the resource bundle
   *
   * @throws MissingResourceException   if the bundle is not found.
   *    This could indicate a packaging problem,
   *    for example properties files missing from a JAR.
   */
  public static final ResourceBundle getBundle(BundleRef bref)
    throws MissingResourceException
  {
    if (bref == null)
       throw new NullPointerException("BundleRef");

    // Relying on the system default locale is a serious limitation
    // in a client/server context. A server may have to perform lookups
    // in the locales requested by clients.
    Locale loc = Locale.getDefault();

    ResourceBundle bundle = ResourceBundle.getBundle
      (bref.getBundleName(), loc, bref.getClassLoader());

    return bundle;
  }

}
