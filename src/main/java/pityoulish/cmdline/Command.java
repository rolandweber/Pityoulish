/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;


/**
 * Represents a command that can be invoked from the command line.
 * This interface is expected to be implemented by an <code>enum</code>.
 */
public interface Command
{
  /**
   * The minimum number of command-specific arguments.
   */
  public int getMinArgs()
    ;

  /**
   * The maximum number of command-specific arguments.
   */
  public int getMaxArgs()
    ;

}
