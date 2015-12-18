/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.i18n;

import java.util.ListResourceBundle;


/**
 * Catalog data for unit tests.
 * Typically, catalgos are implemented as properties files
 * and contain only string resources. To simulate some error conditions,
 * the test resources are implemented as a class instead.
 */
public class UnitTestCatalogData extends ListResourceBundle
{
  public final static String pattern0 =
    "''Pattern without arguments.''";
  public final static String pattern1 =
    "-{0}-";
  public final static String pattern2 =
    "Param {1} before {0}.";

  public final static String textA =
    "Text A.";
  public final static String textB =
    "Boo!";

  public final static Object obj =
    new Object();

  protected Object[][] getContents()
  {
    return new Object[][] {
      // UNDEFINED remains undefined
      { "PATTERN_0", pattern0 },
      { "PATTERN_1", pattern1 },
      { "PATTERN_2", pattern2 },
      { "TEXT_A",    textA },
      { "TEXT_B",    textB },
      { "OBJECT",    obj }
    };
  }
}
