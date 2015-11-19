/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.outtake;


/**
 * Marker for removed code sections.
 */
public final class Missing extends RuntimeException
{
  private Missing(String msg)
  {
    super(msg);
  }


  /**
   * Called from where code is missing.
   *
   * @param what   a message about what is missing
   */
  public static void here(String what)
  {
    // PYL:keep
    // If you're looking for what's missing, this is the wrong place.
    // The second level of the stack trace tells you from where
    // this method has been called. That's where code is missing.
    // PYL:cut
    if (what != null)
       return;
    // PYL:end

    throw new Missing(what);
  }


  /**
   * Does nothing, but lets the compiler think an exception might be thrown.
   * Needed if a <code>catch</code> block handles an exception, but the code
   * that throws it is taken out for an exercise.
   *
   * @param what        the class of the exception that might be thrown
   */
  public static <X extends Exception> void pretend(Class<X> what)
    throws X
  {
    // do nothing
  }

}

