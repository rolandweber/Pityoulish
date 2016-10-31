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
   * <li><b>open</b> an outlet</li>
   * <li><b>send</b> a direct message</li>
   * <li><b>unpublish</b> the outlet</li>
   * </ul>
   */
  public enum OutletCommand implements Command
  {
    OPEN(2,2), SEND(3,3), UNPUBLISH(1,1);

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
      case OPEN: {
        String ticket = args[0];
        int seconds = parseSeconds(args[1]);

        if (seconds > 0)
           outletHandler.openOutlet(ticket, seconds);
        else
           status = 2;
      } break;


      case SEND: {
        String   originator = args[0];
        String[] recipients = parseRecipients(args[1]);
        String   text       = args[2];
        //@@@ sanity check on originator and text?

        // beware the order of arguments, different from the command line
        if (recipients != null)
           outletHandler.sendMessage(originator, text, recipients);
        else
           status = 2;
      } break;


      case UNPUBLISH:
        outletHandler.unpublishOutlet(args[0]); // arg: ticket
        break;


      default:
        // not reachable unless through programming errors
        throw new UnsupportedOperationException(String.valueOf(cmd));
     }

    return status;
  }


  /**
   * Parses the "seconds" argument for the Open Outlet operation.
   * In case of a problem, an error message is printed and
   * a negative value returned.
   *
   * @param arg   the string to parse
   *
   * @return   the seconds, or negative if invalid
   */
  protected int parseSeconds(String arg)
  {
    int seconds = 0;

    try {
      seconds = Integer.parseInt(arg);

      if (seconds < 1)
       {
         System.err.println(Catalog.CMDLINE_BAD_SECONDS_1.format(arg));
         seconds = -1;
       }

    } catch (Exception x) {
      System.err.println(Catalog.CMDLINE_BAD_SECONDS_1.format(arg));
      System.err.println(x);
      seconds = -1;
    }

    return seconds;
  }


  /**
   * Parses the "recipients" argument for the Send Message operation.
   * In case of a problem, an error message is printed and
   * <code>null</code> returned.
   *
   * @param arg   the string to parse
   *
   * @return   the recipients, or <code>null</code> if invalid
   */
  protected String[] parseRecipients(String arg)
  {
    String[] recipients = arg.split(",");

    if (recipients.length < 1)
     {
       System.err.println(Catalog.CMDLINE_BAD_RECIPIENTS_1.format(arg));
       recipients = null;
     }
    else
     {
       for (String recipient : recipients)
          if ((recipient.length() < 1) ||
              ("*".equals(recipient) && recipients.length > 1))
           {
             System.err.println(Catalog.CMDLINE_BAD_RECIPIENTS_1.format(arg));
             recipients = null;
             break;
           }
     }

    return recipients;
  }

}
