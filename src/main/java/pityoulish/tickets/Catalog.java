/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

import pityoulish.i18n.CatalogHelper;
import pityoulish.i18n.TextEntry;


/**
 * Catalog of localizable texts and patterns.
 * The convention used in this package is:
 * <ol>
 * <li>{@link TicketException} uses NLS-enabled messages,
 *     because they are destined for the user.
 *     </li>
 * <li>Runtime exceptions, such as IllegalArgumentException,
 *     use hard-coded messages in English.
 *     They are destined for developers using this API,
 *     and should be resolved before the code is released to users.
 *     </li>
 * </ol>
 */
public enum Catalog implements TextEntry
 {
   WRONG_TICKET_MANAGER,
   WRONG_TOKEN,
   WRONG_USERNAME,
   WRONG_NETWORK_ADDRESS,
   WRONG_NETWORK_HOST,
   TICKET_NOT_FOUND_1,
   TICKET_EXPIRED,
   USER_ALREADY_HAS_TICKET_1,
   ADDRESS_ALREADY_HAS_TICKET_1,
   HOST_ALREADY_HAS_TICKET_1,

   USERNAME_EMPTY,
   USERNAME_TOO_SHORT_1,
   USERNAME_TOO_LONG_1,
   USERNAME_BAD_CHARACTER,
   TOKEN_EMPTY
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
