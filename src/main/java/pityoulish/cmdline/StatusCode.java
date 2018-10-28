/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;


/**
 * A modifiable status code.
 * Just a wrapper around an <code>int</code>.
 */
public class StatusCode
{
  public int code;

  /**
   * Creates a new status code object with the given value.
   *
   * @param sc   the status code value
   */
  public StatusCode(int sc)
  {
    code = sc;
  }

  /** Creates a new status code with value 0 (success). */
  public StatusCode()
  {
    this(0);
  }

}
