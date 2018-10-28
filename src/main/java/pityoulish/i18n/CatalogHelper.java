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
  /**
   * The platform-specific EOL sequence.
   * Looked up when needed, then cached here.
   */
  private static String PLATFORM_EOL;


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

      if (pattern.length() > 0)
       {
         // This call parses the pattern. If patterns are used frequently, they
         // should be parsed only once and the resulting MessageFormat cached.
         // That's not necessary for the programming exercises though.
         result = MessageFormat.format(pattern, params);
       }
      else
       {
         // an empty pattern is an invalid catalog entry, provide a fallback
         result = fallbackFormat(tref, params);
       }

    } catch (MissingResourceException mre) {
      // the lookup failed, provide a fallback
      result = fallbackFormat(tref, params);
    }
    // ClassCastException from bundle.getString intentionally not handled here,
    // because TextRef should always refer to a text.
    // IllegalArgumentException intentionally not handled here,
    // because it indicates a bad resource or a mismatch
    // between resource bundle and code.

    return result;
  }


  /**
   * Formats parameters in case a pattern is missing.
   *
   * @param tref        text ref of the missing pattern
   * @param params      parameters to format
   *
   * @return the arguments in fallback format
   */
  protected static final String fallbackFormat(TextRef tref, Object... params)
  {
    StringBuilder sb = new StringBuilder(80);
    sb.append(tref.getBundleName()).append("::").append(tref.getKey());
    for(Object param : params)
       sb.append(" \"").append(param).append("\"");

    return sb.toString();
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
    // ClassCastException from bundle.getString intentionally not handled here,
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


  /**
   * Obtains the numeric suffix of a string.
   * This supports the naming convention:
   * <ul>
   * <li>The lookup key for a pattern that formats <i>n</i> parameters
   *     ends with '_<i>n</i>'.</li>
   * <li>The lookup key for text which is not a formatting pattern
   *     does not end with a numeric suffix.</li>
   * </ul>
   *
   * @param key         a lookup key or other string to interpret
   *
   * @return    the value of the numeric suffix after the last underscore,
   *            or -1 if there is no numeric suffix
   */
  public static final int getNumericSuffix(String key)
  {
    if ((key == null) || (key.length() < 2))
       return -1;

    int result = -1;
    int underscore = key.lastIndexOf("_");
    if (underscore >= 0)
     {
       try {
         String suffix = key.substring(underscore+1);
         result = Integer.parseInt(suffix);
       } catch (NumberFormatException nfx) {
         // no numeric suffix, result remains -1
       }
     }

    return result;
  }


  /**
   * Converts end-of-line to the platform-specific character sequence.
   * In catalog messages, end of line should be represented as LF (\n).
   * If the platform-specific end-of-line sequence is different,
   * typically CR LF on Windows, this method performs the replacement.
   *
   * @param text   the multi-line text in which to replace
   *
   * @return the argument text, with platform-specific end-of-line characters
   */
  public static final String fixEOL(String text)
  {
    if ((text == null) || (text.length() < 1))
       return text;

    String eol = PLATFORM_EOL;
    if (eol == null)
     {
       eol = System.getProperty("line.separator", "\n");
       PLATFORM_EOL = eol;
     }

    if (!"\n".equals(eol))
       text = text.replace("\n", eol);

    return text;
  }

}
