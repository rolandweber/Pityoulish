/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;

import java.io.IOException;


/**
 * Handler to be invoked by {@link ArgsInterpreter}.
 */
public interface CommandHandler
{
  /**
   * Generates a usage description for the supported commands.
   *
   * @param app   where to append the description
   * @param eol   the end-of-line character sequence to use
   *
   * @throws IOException        if appending fails
   */
  public void describeUsage(Appendable app, String eol)
    throws IOException
    ;


  /**
   * Checks whether a command is supported.
   *
   * @param name    the name of the command
   * @param args    the command-specific arguments
   *
   * @return    <code>true</code> if supported, <code>false</code> otherwise
   */
  public boolean supports(String name, String... args)
    ;


  /**
   * Handles a command.
   *
   * @param name    the name of the command
   * @param args    the command-specific arguments
   *
   * @return    the suggested exit code, 0 for success
   *
   * @throws Exception  in case of a problem
   */
  public int handle(String name, String... args)
    throws Exception
    ;

}
