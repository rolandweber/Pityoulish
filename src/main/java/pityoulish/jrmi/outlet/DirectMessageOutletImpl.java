/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

import java.rmi.server.RemoteObject;
import java.util.regex.Pattern;

import pityoulish.jrmi.api.DirectMessageOutlet;


/**
 * Default implementation of {@link MsgOutletHandler}.
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


  // non-javadoc, see interface
  public void deliverMessage(String originator, String text)
  {
    String error = null;
    String print = null;

    //@@@ NLS light

    if ((originator == null) || (originator.length() < 1))
     {
       error = "There is no originator. You're nobody.";
       print = "no originator";
     }
    else if (!ORIGINATOR_PATTERN.matcher(originator).matches())
     {
       error = "Invalid originator, for length or characters.";
       print = "invalid originator";
     }
    else if ((text == null) || (text.length() < 1))
     {
       error = "There is no message. Stop wasting my time.";
       print = "empty";
     }
    else if (text.length() > 160)
     {
       error = "The message is too long, maximum 160 characters.";
       print = "message too long";
     }
    else if (!TEXT_PATTERN.matcher(text).matches())
     {
       error = "Invalid character in message, don't use control characters.";
       print = "invalid message";
     }

    if (error != null)
     {
       System.out.println("--- error: "+print);
       // Throwing an exception that is available in every JVM is safe,
       // even for remote calls.
       throw new IllegalArgumentException(error);
     }

    System.out.println("--- from '"+originator+"' (unauthenticated)");
    System.out.println(text);
  }


  // non-javadoc, see interface
  public void ping()
  {
    System.out.println("--- ping");
  }

}
