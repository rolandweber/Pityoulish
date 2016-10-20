/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;

import java.io.IOException;

import pityoulish.cmdline.Command;
import pityoulish.cmdline.CommandHandlerBase;


/**
 * Command dispatcher for the Message Board client.
 * Interprets command-line arguments, then calls the applicable method of
 * {@link MsgBoardClientHandler}.
 */
public class MsgBoardCommandDispatcher
  extends CommandHandlerBase<MsgBoardCommandDispatcher.MsgBoardCommand>
{
  /** The handler to dispatch to. */
  public final MsgBoardClientHandler mbcHandler;


  /**
   * List of the client commands.
   * <ul>
   * <li><b>list</b> messages</li>
   * <li><b>put</b> a message</li>
   * <li>obtain <b>ticket</b></li>
   * <li><b>return</b> ticket</li>
   * <li><b>replace</b> ticket</li>
   * </ul>
   */
  public enum MsgBoardCommand implements Command
  {
    LIST(1,2), PUT(2,2), TICKET(1,1), RETURN(1,1), REPLACE(1,1);

    public final int minArgs;
    public final int maxArgs;

    private MsgBoardCommand(int mina, int maxa)
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
   * @param mbch   the handler to dispatch to
   */
  public MsgBoardCommandDispatcher(MsgBoardClientHandler mbch)
  {
    super(MsgBoardCommand.class);

    if (mbch == null)
       throw new NullPointerException("MsgBoardClientHandler");

    mbcHandler = mbch;
  }


  // non-javadoc, see interface CommandHandler
  public void describeUsage(Appendable app)
    throws IOException
  {
    app.append(Catalog.COMMAND_USAGE.lookup());
  }


  // non-javadoc, see base class
  protected int handleCommand(MsgBoardCommand cmd, String... args)
    throws Exception
  {
    int status = 0;
    switch(cmd)
     {
      case LIST: {
        int    limit  = parseLimit(args[0]);
        String marker = (args.length > 1) ? args[1] : null;

        if (limit >= 0)
           mbcHandler.listMessages(limit, marker);
        else
           status = 2;
      } break;

      case PUT:
        mbcHandler.putMessage(args[0], args[1]); // args: ticket, text
        break;

      case TICKET:
        mbcHandler.obtainTicket(args[0]); // arg: username
        break;

      case RETURN:
        mbcHandler.returnTicket(args[0]); // arg: ticket
        break;

      case REPLACE:
        mbcHandler.replaceTicket(args[0]); // arg: ticket
        break;

      default:
        // not reachable unless through programming errors
        throw new UnsupportedOperationException(String.valueOf(cmd));
     }

    return status;
  }


  /**
   * Parses the "limit" argument for the List Messages operation.
   * In case of a problem, an error message is printed and
   * a negative value returned.
   * Violation of the range 1..127 is detected here and treated as a problem.
   *
   * @param arg   the string to parse
   *
   * @return   the limit, or negative if invalid
   */
  protected int parseLimit(String arg)
  {
    int limit = 0;

    try {
      limit = Integer.parseInt(arg);

      if ((limit < 1) || (limit > 127))
       {
         System.err.println(Catalog.CMDLINE_BAD_LIMIT_1.format(arg));
         limit = -1;
       }

    } catch (Exception x) {
      System.err.println(Catalog.CMDLINE_BAD_LIMIT_1.format(arg));
      System.err.println(x);
      limit = -1;
    }

    return limit;
  }

}

