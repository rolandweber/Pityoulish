/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.i18n;

/**
 * Catalog for unit tests.
 */
public enum UnitTestCatalog implements TextEntry
 {
   UNDEFINED,
     PATTERN_0,
     PATTERN_1,
     PATTERN_2,
     TEXT_A,
     TEXT_B,
     OBJECT;

   private final static String bundleName =
     UnitTestCatalog.class.getName()+"Data";

   private final int numParams;

   private UnitTestCatalog()
    {
      numParams = CatalogHelper.getNumericSuffix(super.name());
    }


   // see interface TextEntry
   public final String format(Object... params)
   {
     return CatalogHelper.format(this, params);
   }

   // see interface TextEntry
   public final String lookup()
    {
      return CatalogHelper.lookup(this);
    }


   // see interface BundleRef
   public final String getBundleName()
    {
      return bundleName;
    }

   // see interface BundleRef
   public final ClassLoader getClassLoader()
    {
      return getClass().getClassLoader();
    }


   // see interface TextRef
   public final String getKey()
    {
      return name();
    }

   // see interface TextRef
   public final int getParamCount()
    {
      return numParams;
    }

 }
