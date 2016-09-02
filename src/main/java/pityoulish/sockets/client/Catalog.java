/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import pityoulish.i18n.CatalogHelper;
import pityoulish.i18n.TextEntry;


/**
 * Catalog of localizable texts and patterns.
 */
public enum Catalog implements TextEntry
 {
   BACKEND_ARGS_1,
   COMMAND_USAGE,

   INVALID_TOP_TLV_HEADER_0,
   INVALID_TOP_TLV_TYPE_1,
   INVALID_TOP_TLV_LENGTH_0,
   INCOMPLETE_TOP_TLV_DATA_0,

   INVALID_TLV_LENGTH_2,
   INVALID_TLV_VALUE_3,
   INVALID_TLV_STRING_ENC_3,

   UNEXPECTED_TLV_2,
   UNEXPECTED_TLV_3,
   DUPLICATE_TLV_2,
   MISSING_NESTED_TLV_3,
   OVERLONG_TLV_2,

   INFO_SPLITTING_AT_2,
   INFO_RECEIVING_0,
   INFO_STILL_MISSING_1,
   RECEIVE_INITIAL_BLOCK_TOO_SMALL_0,
   RECEIVE_BAD_TYPE_1,
   RECEIVE_BAD_LEN_OF_LEN_1,
   RECEIVE_TOO_LONG_2,
   RECEIVE_INCOMPLETE_0,

   CMDLINE_BAD_PORT_1,
   CMDLINE_BAD_LIMIT_1
   ;


   private final static String 
     bundleName = Catalog.class.getName() + "Data";

   private final int numParams;

   private Catalog()
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


   public final static String fixEOL(String text)
   {
     return CatalogHelper.fixEOL(text);
   }

 }
