/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.logutil;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.logging.LogManager;


/**
 * Helper for logging configuration.
 * A system property is used to detect which logging configuration should
 * be applied. If that configuration is found, it is applied.
 * Otherwise, the default Java logging configuration remains unmodified.
 * {@link #LOGGING_PROPERTY_NAME} is interpreted as follows:
 * <ol>
 * <li>If the value contains a '.', a properties file with the given name
 *     is searched in the file system.
 * </li>
 * <li>If the value does not contain a '.', a properties file with the name
 *     "logconfig.<i>value</i>.properties" is searched in the classpath.
 *     Typically, these properties files are bundled in the JAR. The file
 *     is first searched in the caller's package, near <code>Main</code>.
 *     Then it is searched near in this package.
 * </li>
 * <li>If {@link #LOGGING_PROPERTY_NAME} is not set,
 *     {@link #LOGGING_PROPERTY_DEFAULT} is the default.
 *     To leave the Java logging configuration unmodified,
 *     set the property to an empty string.
 * </li>
 * </ol>
 */
public final class LogConfig
{
  /** The system property indicating the logging configuration. */
  public final static String LOGGING_PROPERTY_NAME = "pityoulish.log";

  /** The default value for {@link #LOGGING_PROPERTY_NAME}. */
  public final static String LOGGING_PROPERTY_DEFAULT = "silent";


  /** Disabled default constructor. */
  private LogConfig()
  {
    throw new UnsupportedOperationException();
  }


  /**
   * Configures logging, if applicable.
   *
   * @param cls   the class near which to search for bundled properties,
   *              or <code>null</code>
   *
   * @throws Exception   in case of a problem
   */
  public static void configure(Class cls)
    throws Exception //@@@ really?
  {
    String configname = System.getProperty(LOGGING_PROPERTY_NAME,
                                           LOGGING_PROPERTY_DEFAULT);
    if ((configname == null) || "".equals(configname))
       return;

    InputStream cfgstream = null;
    try {
      if (configname.indexOf(".") < 0)
       {
         // search for a bundled properties file
         configname = "logconfig." + configname + ".properties";

         if (cls != null)
            cfgstream = cls.getResourceAsStream(configname);
         if (cfgstream == null)
            cfgstream = LogConfig.class.getResourceAsStream(configname);
       }
      else
       {
         System.out.println("@@@ logging config file: "+configname);
         // read the properties from the file system
         cfgstream = new FileInputStream(configname);
       }

      if (cfgstream != null)
         LogManager.getLogManager().readConfiguration(cfgstream);
      else System.out.println("@@@ logging config not found");

      //@@@ what to do about an exception? log? let pass?
    } finally {
      if (cfgstream != null)
         cfgstream.close();
    }

  }

}
