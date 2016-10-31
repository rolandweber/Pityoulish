/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

import java.io.IOException;

import pityoulish.cmdline.Command;
import pityoulish.cmdline.CommandHandlerBase;


/**
 * Command dispatcher for the Message Outlet handler.
 * Interprets command-line arguments, then calls the applicable method of
 * {@link MsgOutletHandler}.
 */
public class MsgOutletCommandDispatcher
  extends CommandHandlerBase<MsgOutletCommandDispatcher.OutletCommand>
{
  /** The handler to dispatch to. */
  public final MsgOutletHandler outletHandler;


  /**
   * List of the outlet commands.
   * <ul>
   * <li><b>receive</b> direct messages</li>
   * <li><b>send</b> a direct message</li>
   * <li><b>unpublish</b> the endpoint</li>
   * </ul>
   */
  public enum OutletCommand implements Command
  {
    RECEIVE(2,2), SEND(3,3), UNPUBLISH(1,1);

    public final int minArgs;
    public final int maxArgs;

    private OutletCommand(int mina, int maxa)
     {
       minArgs = mina;
       maxArgs = maxa;
     }

    public final int getMinArgs() { return minArgs; }
    public final int getMaxArgs() { return maxArgs; }
  }



  /**
   * Creates a new command dispatcher.
   *
   * @param moh    the handler to dispatch to
   */
  public MsgOutletCommandDispatcher(MsgOutletHandler moh)
  {
    super(OutletCommand.class);

    if (moh == null)
       throw new NullPointerException("MsgOutletHandler");

    outletHandler = moh;
  }


  // non-javadoc, see interface CommandHandler
  public void describeUsage(Appendable app)
    throws IOException
  {
    app.append(Catalog.COMMAND_USAGE.lookup());
  }


  // non-javadoc, see base class
  protected int handleCommand(OutletCommand cmd, String... args)
    throws Exception
  {
    int status = 0;
    switch(cmd)
     {
      case RECEIVE:
      case SEND:
      case UNPUBLISH:
       //@@@ cases to be implemented
        if (false) break; //@@@ never

      default:
        // not reachable unless through programming errors
        throw new UnsupportedOperationException(String.valueOf(cmd));
     }

    return status;
  }

}

