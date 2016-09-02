/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.i18n.CatalogHelper;
import pityoulish.i18n.TextEntry;


/**
 * Catalog of localizable texts and patterns.
 */
public enum Catalog implements TextEntry
 {
   USAGE,
   SYSMSG_OPEN,
   SYSMSG_CAPACITY_1,

   INVALID_TOP_TLV_HEADER_0,
   INVALID_TOP_TLV_TYPE_1,
   INVALID_TOP_TLV_LENGTH_0,
   INCOMPLETE_TOP_TLV_DATA_0,

   INVALID_TLV_LENGTH_2,
   INVALID_TLV_VALUE_3,
   INVALID_TLV_STRING_ENC_3,

   UNEXPECTED_TLV_2,
   DUPLICATE_TLV_2,
   MISSING_NESTED_TLV_3,
   OVERLONG_TLV_2,

   NO_DISABLE_SEND_BUFFER_0,
   RECEIVE_FROM_1,
   RECEIVE_INITIAL_BLOCK_TOO_SMALL_0,
   RECEIVE_BAD_TYPE_1,
   RECEIVE_BAD_LEN_OF_LEN_1,
   RECEIVE_TOO_LONG_2,
   RECEIVE_DEADLINE_EXPIRED_0,
   RECEIVE_INCOMPLETE_0
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


   /**
    * Formats this text entry into a {@link ProtocolException}.
    * The exception is returned as an object,
    * throwing it is left to the caller.
    *
    * @param params     the parameters for formatting
    *
    * @return an exception object with the formatted message
    */
   public final ProtocolException asPX(Object... params)
   {
     String msg = format(params);
     return new ProtocolException(msg);
   }


   /**
    * Formats this text entry into a {@link ProtocolException},
    * with root cause.
    * The exception is returned as an object,
    * throwing it is left to the caller.
    *
    * @param cause      the root cause, or <code>null</code>
    * @param params     the parameters for formatting
    *
    * @return   an exception object with the formatted message and
    *           given root cause
    */
   public final ProtocolException asPXwithCause(Throwable cause,
                                                Object... params)
   {
     String msg = format(params);
     return new ProtocolException(msg, cause);
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
