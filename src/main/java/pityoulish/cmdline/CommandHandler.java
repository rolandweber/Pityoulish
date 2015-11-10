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
   * Handles a command, if it is supported.
   *
   * @param status  OUTPUT: the suggested exit code, initially 0 (success)
   * @param name    the name of the command
   * @param args    the command-specific arguments
   *
   * @return    <code>true</code> if the command was handled,
   *            <code>false</code> if it is not supported.
   * If the command is handled, the <code>status</code> argument contains the
   * suggested exit code. If the command is not supported, the value of the
   * <code>status</code> argument remains unchanged.
   *
   * @throws Exception  if the command is supported, but there was a problem
   * while handling it. The value of the <code>status</code> argument is
   * undefined.
   */
  public boolean handle(StatusCode status, String name, String... args)
    throws Exception
    ;
}
