/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.follow;

import java.io.IOException;

import pityoulish.cmdline.Command;
import pityoulish.cmdline.SingleCommandHandlerBase;

import pityoulish.mbclient.MsgBoardClientHandler;


/**
 * Command handler for the Follow-the-Board client.
 * The client supports only a single command.
 */
public class FollowTheBoardHandler extends SingleCommandHandlerBase
{
  /** The handler to invoke for communication. */
  public final MsgBoardClientHandler mbcHandler;

  /** The batch tracker, for accessing batch metadata. */
  public final MessageBatchTracker batchTracker;


  /** The polling interval, in seconds. */
  protected int pollingSeconds;

  protected static final int DEFAULT_POLLING_SECONDS = 8;

  /** The marker from which to list subsequent messages. Initially null. */
  protected String listMarker;


  /**
   * Creates a new command handler.
   *
   * @param mbch   the handler to invoke for communication
   * @param mbt    the tracker for batch metadata
   */
  public FollowTheBoardHandler(MsgBoardClientHandler mbch,
                               MessageBatchTracker mbt)
  {
    super(0, 1); // minArgs, maxArgs

    if (mbch == null)
       throw new NullPointerException("MsgBoardClientHandler");
    if (mbt == null)
       throw new NullPointerException("MessageBatchTracker");

    mbcHandler = mbch;
    batchTracker = mbt;
  }


  // non-javadoc, see interface CommandHandler
  public void describeUsage(Appendable app)
    throws IOException
  {
    app.append(Catalog.COMMAND_USAGE.lookup());
  }


  // non-javadoc, see base class
  protected int handleCommand(String... args)
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
    else
     {
       pollingSeconds = DEFAULT_POLLING_SECONDS;
     }

    pollForMessages();
    // never reached

    return 0; // status OK
  }


  /**
   * Polls for messages, forever.
   * Actually, until an exception is thrown, for whatever reason.
   *
   * @throws Exception  in case of a problem
   */
  protected void pollForMessages()
    throws Exception
  {
    while (true)
     {
       listAvailableMessages();

       try {
         Thread.sleep(pollingSeconds * 1000L);
       } catch (InterruptedException ix) {
         // ignore
       }
     }
  }


  /**
   * Lists as many messages as are available.
   * If {@link #listMarker} is set, messages are listed from there.
   * The marker is updated here, for subsequent calls.
   *
   * @throws Exception  in case of a problem
   */
  protected void listAvailableMessages()
    throws Exception
  {
    final int BATCH_SIZE = 127;

    boolean more = true;
    while (more)
     {
       mbcHandler.listMessages(BATCH_SIZE, listMarker);

       more       = (batchTracker.getSize() >= BATCH_SIZE);
       listMarker = batchTracker.getMarker();
     }
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

