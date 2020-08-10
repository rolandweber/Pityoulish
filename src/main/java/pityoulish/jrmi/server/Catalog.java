/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.server;

import java.util.logging.Logger;

import pityoulish.i18n.CatalogHelper;
import pityoulish.i18n.TextEntry;
import pityoulish.logutil.Log;

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

   OUTLET_UNREACHABLE_1,
   OUTLET_STILL_ALIVE_1,
   OUTLET_CONCURRENTLY_PUBLISHED_1,
   OUTLET_NONE_1,

   LIMIT_OUT_OF_RANGE_1,

   REPORT_CREATE_REGISTRY_1,
   REPORT_JRMI_HOSTNAME_1,
   REPORT_JRMI_HOSTNAME_PROPERTY_1,
   REPORT_LIST_MESSAGES_2,
   REPORT_PUT_MESSAGE_1,
   REPORT_OBTAIN_TICKET_2,
   REPORT_RETURN_TICKET_1,
   REPORT_REPLACE_TICKET_2,
   REPORT_PUBLISH_OUTLET_1,
   REPORT_UNPUBLISH_OUTLET_1
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
    * <br><b>Note:</b>  The client side might be missing classes necessary
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


   /**
    * Logs an exception.
    * A summary of the exception is also printed to the console.
    * The exception is returned, so that the caller can throw it like:
    * <pre>
    *     throw Catalog.log(logger, "context", Catalog.asApiX(...));
    * </pre>
    *
    * <b>Note:</b> This method has nothing to do with the catalog.
    * But because the catalog already has methods to create APIExceptions,
    * it makes sense to put the helper for logging them here as well.
    * There is no implicit logging of the exceptions when they are created.
    * Logging should be done at the point where the exception is thrown.
    *
    * @param l     where to log
    * @param ctx   context information for the exception
    * @param x     the exception to log
    * @param <X>   the type of exception to log
    *
    * @return the <code>x</code> argument
    */
   public final static <X extends Throwable> X log(Logger l, String ctx, X x)
   {
     Log.log(l, ctx, x);
     System.out.println(x.toString());
     return x;
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
