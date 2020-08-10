/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.logutil;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Helper methods for logging.
 */
public final class Log
{
  /** Disabled default constructor. */
  private Log()
  {
    throw new UnsupportedOperationException();
  }


  /**
   * Finds or creates a package-level logger.
   * A common approach is to use class names as logger names.
   * To allow for obfuscation of class names, package names are used here.
   * The downside is that the logging configuration becomes coarser grained.
   *
   * @param cls   the class for which to obtain a package-level logger.
   *              The class name will be cut off to get the package name.
   *
   * @return the package-level logger
   */
  public static Logger getPackageLogger(Class cls)
  {
    if (cls == null)
       throw new NullPointerException("Class");

    String name = cls.getName();
    name = name.substring(0, name.lastIndexOf("."));

    return Logger.getLogger(name);
  }


  /**
   * Logs an exception. The exception is returned, so
   * it can be logged and thrown in a single statement.
   * <pre>
   *   throw Log.log(logger, "about", new Exception("why"));
   * </pre>
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
    // If we don't pass a sourceClass and sourceMethod argument,
    // JUL will determine this class and method, which are pointless.
    // Use the logger name as sourceClass, even if it is something else.
    l.logp(Level.WARNING, l.getName(), ctx,
           (x != null) ? x.getMessage() : null, x);
    return x;
  }

}
