/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

import pityoulish.i18n.CatalogHelper;
import pityoulish.i18n.TextEntry;


/**
 * Catalog of localizable texts and patterns.
 */
public enum Catalog implements TextEntry
 {
   BACKEND_ARGS_1,
   COMMAND_USAGE,

   CMDLINE_BAD_PORT_1,
   CMDLINE_BAD_SECONDS_1,
   CMDLINE_BAD_RECIPIENTS_1,

   MISSING_ORIGINATOR_0,
   BAD_ORIGINATOR_0,
   MISSING_MESSAGE_0,
   MESSAGE_TOO_LONG_1,
   BAD_MESSAGE_0,

   REPORT_JRMI_HOSTNAME_1,
   REPORT_JRMI_HOSTNAME_PROPERTY_1,
   REPORT_LIST_OUTLETS_0,
   REPORT_NO_OUTLET_0,
   REPORT_SENDING_TO_1,
   REPORT_OPEN_OUTLET_1,
   REPORT_OPEN_STUB_1,
   REPORT_OPEN_PUBLISH_0,
   REPORT_OPEN_WAITING_1,
   REPORT_OPEN_UNPUBLISH_0,
   REPORT_PING_0,
   REPORT_MESSAGE_2,
   REPORT_BAD_DELIVERY_1
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
