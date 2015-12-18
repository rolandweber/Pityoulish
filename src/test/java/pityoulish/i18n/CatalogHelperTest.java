/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.i18n;

import org.junit.*;
import static org.junit.Assert.*;

public class CatalogHelperTest
{
  @Test public void lookup_UNDEFINED()
    throws Exception
  {
    String text = UnitTestCatalog.UNDEFINED.lookup();
    assertEquals("wrong text",
                 "pityoulish.i18n.UnitTestCatalogData::UNDEFINED", text);
  }

  @Test public void lookup_PATTERN_0()
    throws Exception
  {
    String text = UnitTestCatalog.PATTERN_0.lookup();
    assertEquals("wrong text", UnitTestCatalogData.pattern0, text);
  }

  @Test public void lookup_PATTERN_1()
    throws Exception
  {
    String text = UnitTestCatalog.PATTERN_1.lookup();
    assertEquals("wrong text", UnitTestCatalogData.pattern1, text);
  }

  @Test public void lookup_PATTERN_2()
    throws Exception
  {
    String text = UnitTestCatalog.PATTERN_2.lookup();
    assertEquals("wrong text", UnitTestCatalogData.pattern2, text);
  }

  @Test public void lookup_TEXT_A()
    throws Exception
  {
    String text = UnitTestCatalog.TEXT_A.lookup();
    assertEquals("wrong text", UnitTestCatalogData.textA, text);
  }

  @Test public void lookup_TEXT_B()
    throws Exception
  {
    String text = UnitTestCatalog.TEXT_B.lookup();
    assertEquals("wrong text", UnitTestCatalogData.textB, text);
  }

  @Test public void lookup_OBJECT()
    throws Exception
  {
    try {
      String text = UnitTestCatalog.OBJECT.lookup();
      fail("unexpected success: "+text);
    } catch (ClassCastException cce) {
      // expected
    }
  }



  @Test public void format_PATTERN_0_0()
    throws Exception
  {
    String text = UnitTestCatalog.PATTERN_0.format();
    // two single quotes '' are substituted by one single quote '
    assertEquals("wrong text",
                 "'Pattern without arguments.'", text);
  }


  @Test public void format_PATTERN_0_1()
    throws Exception
  {
    try {
    String text = UnitTestCatalog.PATTERN_0.format("something");
      fail("unexpected success: "+text);
    } catch (IllegalArgumentException iax) {
      // expected
    }
  }


  @Test public void format_PATTERN_1_0()
    throws Exception
  {
    try {
    String text = UnitTestCatalog.PATTERN_1.format();
      fail("unexpected success: "+text);
    } catch (IllegalArgumentException iax) {
      // expected
    }
  }


  @Test public void format_PATTERN_1_1()
    throws Exception
  {
    String text = UnitTestCatalog.PATTERN_1.format("something");
    // two single quotes '' are substituted by one single quote '
    assertEquals("wrong text", "-something-", text);
  }


  @Test public void format_PATTERN_2_2()
    throws Exception
  {
    String text = UnitTestCatalog.PATTERN_2.format("One", "Two");
    // two single quotes '' are substituted by one single quote '
    assertEquals("wrong text", "Param Two before One.", text);
  }


  @Test public void format_TEXT_A()
    throws Exception
  {
    try {
      String text = UnitTestCatalog.TEXT_A.format();
      fail("unexpected success: "+text);
    } catch (IllegalArgumentException iax) {
      // expected
    }
  }


}
