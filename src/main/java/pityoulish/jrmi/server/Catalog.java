/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.server;

import pityoulish.i18n.CatalogHelper;
import pityoulish.i18n.TextEntry;

import pityoulish.jrmi.api.APIException;


/**
 * Catalog of localizable texts and patterns.
 */
public enum Catalog implements TextEntry
 {
   USAGE,
   SYSMSG_OPEN,
   SYSMSG_CAPACITY_1,

   TICKET_USED_UP_1,
   TICKET_BAD_2,
   TICKET_DENIED_2,
   TICKET_REPLACE_DENIED_2,

   REPORT_CREATE_REGISTRY_1,
   REPORT_JRMI_HOSTNAME_1,
   REPORT_JRMI_HOSTNAME_PROPERTY_1
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
    * Formats this text entry into an {@link APIException}.
    * The exception is returned as an object,
    * throwing it is left to the caller.
    *
    * @param params     the parameters for formatting
    *
    * @return an exception object with the formatted message
    */
   public final APIException asApiX(Object... params)
   {
     String msg = format(params);
     return new APIException(msg);
   }


   /**
    * Formats this text entry into an {@link APIException},
    * with root cause.
    * The exception is returned as an object,
    * throwing it is left to the caller.
    *
    *
    * @param cause      the root cause, or <code>null</code>.
    * <br/><b>Note:</b> The client side might be missing classes necessary
    *                   to deserialize the root cause!
    * @param params     the parameters for formatting
    *
    * @return   an exception object with the formatted message and
    *           given root cause
    */
   public final APIException asApiXWithCause(Throwable cause,
                                             Object... params)
   {
     String msg = format(params);
     return new APIException(msg, cause);
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
