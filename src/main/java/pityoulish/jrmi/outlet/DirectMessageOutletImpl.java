/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

import java.rmi.server.RemoteObject;
import java.util.regex.Pattern;

import pityoulish.jrmi.api.dm.DirectMessageOutlet;


/**
 * Default implementation of {@link DirectMessageOutlet}.
 */
public class DirectMessageOutletImpl extends RemoteObject
  implements DirectMessageOutlet
{
  /** The pattern for originators. Includes a length range. */
  protected final static Pattern ORIGINATOR_PATTERN;

  /**
   * The pattern for message content.
   * The length is checked separately, it could be made configurable.
   */
  protected final static Pattern TEXT_PATTERN;

  static {
    try {
      ORIGINATOR_PATTERN = Pattern.compile("[A-Za-z0-9_]{6,12}");
      TEXT_PATTERN       = Pattern.compile("\\P{Cntrl}+"); // no control chars
    } catch (Exception x) {
      throw new RuntimeException(x);
    }
  }

  /** The maximum length of a message, in characters. */
  protected final static int MAX_MSGESSAGE_LENGTH = 160;


  // non-javadoc, see interface
  public void deliverMessage(String originator, String text)
  {
    String error = null;

    if ((originator == null) || (originator.length() < 1))
     {
       error = Catalog.MISSING_ORIGINATOR_0.format();
     }
    else if (!ORIGINATOR_PATTERN.matcher(originator).matches())
     {
       error = Catalog.BAD_ORIGINATOR_0.format();
     }
    else if ((text == null) || (text.length() < 1))
     {
       error = Catalog.MISSING_MESSAGE_0.format();
     }
    else if (text.length() > MAX_MSGESSAGE_LENGTH)
     {
       error = Catalog.MESSAGE_TOO_LONG_1.format(MAX_MSGESSAGE_LENGTH);
     }
    else if (!TEXT_PATTERN.matcher(text).matches())
     {
       error = Catalog.BAD_MESSAGE_0.format();
     }

    if (error != null)
     {
       System.out.println(Catalog.REPORT_BAD_DELIVERY_1.format(error));
       // Throwing an exception that is available in every JVM is safe,
       // even for remote calls.
       throw new IllegalArgumentException(error);
     }

    System.out.println(Catalog.REPORT_MESSAGE_2.format(originator, text));
  }


  // non-javadoc, see interface
  public void ping()
  {
    System.out.println(Catalog.REPORT_PING_0.format());
  }

}
