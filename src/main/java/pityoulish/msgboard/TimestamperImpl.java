/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Default implementation of {@link Timestamper}.
 */
public class TimestamperImpl implements Timestamper
{
  public final static String FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  protected SimpleDateFormat format;


  /**
   * Creates a new timestamper implementation.
   */
  public TimestamperImpl()
  {
    TimeZone utc = TimeZone.getTimeZone("UTC");
    format = new SimpleDateFormat(FORMAT_STRING);
    format.setTimeZone(utc);
  }


  /**
   * Obtains a timestamp for the current time.
   * This method calls {@link #getTimestamp(long)}.
   * <p>
   * It is an anti-pattern to let public methods call eachother,
   * because it's hard to tell which methods need to be overridden
   * in derived classes to change the behavior.
   * Declaring this method <code>final</code> makes that clear though.
   * </p>
   */
  public final String getTimestamp()
  {
    return this.getTimestamp(System.currentTimeMillis());
  }


  // non-javadoc, see interface
  public String getTimestamp(long when)
  {
    return format.format(new Date(when));
  }

}
