/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.follow;

import java.io.PrintStream;

import pityoulish.sockets.client.ResponseParser;


/**
 * A {@link pityoulish.sockets.client.ResponseParser.Visitor visitor}
 * that tracks and prints message batches.
 */
public class TrackingVisitorImpl
  implements ResponseParser.Visitor, MessageBatchTracker
{
  /** Where to print to. */
  public final PrintStream out;

  /** The marker of the current or last batch. */
  protected String batchMarker;

  /** The message count of the current or last batch. */
  protected int batchSize;


  /**
   * Creates a new visitor, printing to the given stream.
   *
   * @param ps   where to print the data to, or
   *             <code>null</code> for System.out
   */
  public TrackingVisitorImpl(PrintStream ps)
  {
    out = (ps != null) ? ps : System.out;
  }

  /**
   * Creates a new visitor, printing to System.out.
   */
  public TrackingVisitorImpl()
  {
    this(null);
  }


  // non-javadoc, see interface MessageBatchTracker
  public int getSize()
  {
    return batchSize;
  }

  // non-javadoc, see interface MessageBatchTracker
  public String getMarker()
  {
    return batchMarker;
  }


  // non-javadoc, see interface ResponseParser.Visitor
  public void visitInfo(String text)
  {
    out.println(Catalog.CONSOLE_INFO_TEXT_1.format(text));
  }


  // non-javadoc, see interface ResponseParser.Visitor
  public void visitError(String text)
  {
    out.println(Catalog.CONSOLE_ERROR_TEXT_1.format(text));
  }


  // non-javadoc, see interface ResponseParser.Visitor
  public void enterMessageBatch(String marker, boolean missed)
  {
    batchMarker = marker;
    batchSize   = 0;

    if (missed)
       System.out.println(Catalog.CONSOLE_MESSAGES_MISSED.lookup());
  }


  // non-javadoc, see interface ResponseParser.Visitor
  public void leaveMessageBatch()
  {
    // nothing
  }


  // non-javadoc, see interface ResponseParser.Visitor
  public void visitMessage(String originator, String timestamp, String text)
  {
    batchSize++;
    out.println(Catalog.CONSOLE_MESSAGE_ABOUT_2.format(originator,
                                                       timestamp));
    out.println(text);
  }


  // non-javadoc, see interface ResponseParser.Visitor
  public void visitTicketGrant(String ticket)
  {
    // nothing
  }

}

