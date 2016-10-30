/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;

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
   CMDLINE_BAD_LIMIT_1,

   CONSOLE_MSG_LIST_HEADER,
   CONSOLE_MESSAGES_MISSED,
   CONSOLE_MESSAGE_ABOUT_2,
   CONSOLE_MSG_LIST_FOOTER,
   CONSOLE_MARKER_1,
   CONSOLE_TICKET_1
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
