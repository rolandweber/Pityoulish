/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;

import java.io.PrintStream;

import pityoulish.jrmi.api.MessageItem;
import pityoulish.jrmi.api.MessageList;


/**
 * Default implementation of {@link DataFormatter}.
 * Prints information to System.out.
 */
public class DataFormatterImpl implements DataFormatter
{
  /** Where to print to. */
  public final PrintStream out;


  /**
   * Creates a new data formatter for the given stream.
   *
   * @param ps   where to print the data to, or
   *             <code>null</code> for System.out
   */
  public DataFormatterImpl(PrintStream ps)
  {
    out = (ps != null) ? ps : System.out;
  }

  /**
   * Creates a new data formatter for System.out.
   */
  public DataFormatterImpl()
  {
    this(null);
  }


  public void printMessageList(MessageList msglist)
  {
    if (msglist == null)
       throw new NullPointerException("MessageList");

    out.println(Catalog.CONSOLE_MSG_LIST_HEADER.lookup());

    if (msglist.isDiscontinuous())
       out.println(Catalog.CONSOLE_MESSAGES_MISSED.lookup());

    for (MessageItem msg : msglist.getMessages())
     {
       out.println(Catalog.CONSOLE_MESSAGE_ABOUT_2.format
                   (msg.getOriginator(), msg.getTimestamp()));
       out.println(msg.getText());
     }

    out.println(Catalog.CONSOLE_MSG_LIST_FOOTER.lookup());

    out.println(Catalog.CONSOLE_MARKER_1.format(msglist.getMarker()));
  }


  public void printTicket(String ticket)
  {
    if (ticket == null)
       throw new NullPointerException("ticket");

    out.println(Catalog.CONSOLE_TICKET_1.format(ticket));
  }

}
