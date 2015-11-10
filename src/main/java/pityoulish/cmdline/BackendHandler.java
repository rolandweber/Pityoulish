/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;

import java.io.IOException;


/**
 * Backend handler to be invoked by {@link ArgsInterpreter}.
 * If you have no backend, use {@link None}.
 */
public interface BackendHandler
{
  /**
   * Generates a description for the backend arguments.
   * The description should end with the <code>eol</code> argument.
   *
   * @param app   where to append the description
   * @param eol   the end-of-line character sequence to use
   * @param cmd   what comes after the backend arguments. For example
   *              <pre>&lt;cmd&gt; [&lt;arg&gt; [...]]</pre>
   *
   * @throws IOException        if appending fails
   */
  public void describeUsage(Appendable app, String eol, String cmd)
    throws IOException
    ;


  /**
   * Indicates the expected number of backend arguments.
   *
   * @return the number of arguments before the command word
   */
  public int getArgCount()
    ;


  /**
   * Provides the backend information.
   * A handler may connect to the backend immediately,
   * or store the information for later.
   *
   * @param args        the expected number of backend arguments,
   *                    as specified by {@link #getArgCount}
   *
   * @throws Exception  in case of a problem detected immediately
   */
  public void setBackend(String... args)
    throws Exception
    ;


  /** Handler for a non-existing backend. Expects no arguments at all. */
  public final static class None implements BackendHandler
  {
    public void describeUsage(Appendable app, String eol, String cmd)
      throws IOException
    {
      app.append(cmd).append(eol);
    }

    public int getArgCount()
    {
      return 0;
    }

    public void setBackend(String... args)
    {
      // do nothing
    }
  } // class None

}
