/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.follow;

import java.io.IOException;

import pityoulish.cmdline.Command;
import pityoulish.cmdline.CommandHandlerBase;


/**
 * Command handler for the Follow-the-Board client.
 * The client supports only a single command.
 */
//@@@ TODO: Don't use the command-based ArgsInterpreter.
//@@@       Or give it a mode with an implicit command.
public class FollowTheBoardHandler
  extends CommandHandlerBase<FollowTheBoardHandler.DummyCommand>
{
  protected final RegistryBackendHandler regBackend;

  /** The polling interval, with default value. */
  protected int pollingSeconds = 3;


  /**
   * Dummy list of the only command.
   * <ul>
   * <li><b>follow</b> the board messages</li>
   * </ul>
   */
  public enum DummyCommand implements Command
  {
    FOLLOW;

    public final int getMinArgs() { return 0; }
    public final int getMaxArgs() { return 1; }
  }



  /**
   * Creates a new command dispatcher.
   *
   * @param rbh   the backend handler
   */
  public FollowTheBoardHandler(RegistryBackendHandler rbh)
  {
    super(DummyCommand.class);

    if (rbh == null)
       throw new NullPointerException("RegistryBackendHandler");

    regBackend = rbh;
  }


  // non-javadoc, see interface CommandHandler
  public void describeUsage(Appendable app)
    throws IOException
  {
    app.append(Catalog.COMMAND_USAGE.lookup());
  }


  // non-javadoc, see base class
  protected int handleCommand(DummyCommand cmd, String... args)
    throws Exception
  {
    if (args.length > 0)
     {
       int interval = parsePollingInterval(args[0]);
       if (interval > 0)
          pollingSeconds = interval;
       else
          return 2; // error status
     }

    System.out.println("@@@ should poll messages now");

    return 0; // status OK
  }


  /**
   * Parses the polling interval.
   * In case of a problem, an error message is printed and 0 returned.
   *
   * @param arg   the string to parse
   *
   * @return   the polling interval in seconds, or 0 if invalid
   */
  protected int parsePollingInterval(String arg)
  {
    int interval = 0;

    try {
      interval = Integer.parseInt(arg);

      if (interval < 1)
       {
         System.err.println(Catalog.CMDLINE_BAD_INTERVAL_1.format(arg));
         interval = 0;
       }

    } catch (Exception x) {
      System.err.println(Catalog.CMDLINE_BAD_INTERVAL_1.format(arg));
      System.err.println(x);
      interval = 0;
    }

    return interval;
  }

}

