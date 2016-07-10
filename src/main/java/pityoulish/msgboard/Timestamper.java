/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * A generator for timestamps.
 * See {@link Message#getTimestamp Message.getTimestamp()}
 * for the expected format.
 * <p>
 * Date/time formatting methods in <code>java.text</code> are not thread-safe.
 * Defining a static helper method would either serialize timestamp creation
 * across all threads, or create expensive temporary objects.
 * Defining a helper class that needs to be instantiated allows every board
 * to create and re-use its own timestamper.
 * </p>
 */
public interface Timestamper
{
  /**
   * Generates a timestamp for the current time.
   *
   * @return the timestamp for now
   */
  public String getTimestamp()
    ;


  /**
   * Generates a timestamp for the given time.
   * This method is useful for unit-testing the formatting logic.
   *
   * @param when   the point in time for which to generate a timestamp.
   *               See <code>java.util.Date</code> for the interpretation.
   *
   * @return the timestamp for the argument
   */
  public String getTimestamp(long when)
    ;

}
