/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;


/**
 * A {@link ResponseParser.Visitor visitor} that formats responses.
 * The output can be printed to the console.
 */
public class FormattingVisitorImpl implements ResponseParser.Visitor
{

  // non-javadoc, see interface
  public void visitInfo(String text)
  {
    System.out.println(Catalog.CONSOLE_INFO_TEXT_1.format(text));
  }


  // non-javadoc, see interface
  public void visitError(String text)
  {
    System.out.println(Catalog.CONSOLE_ERROR_TEXT_1.format(text));
  }


  // non-javadoc, see interface
  public void enterMessageBatch(String marker, boolean missed)
  {
    System.out.println(Catalog.CONSOLE_MSG_BATCH_ENTER.lookup());
    System.out.println(Catalog.CONSOLE_MARKER_1.format(marker));
    if (missed)
       System.out.println(Catalog.CONSOLE_MESSAGES_MISSED.lookup());
  }


  // non-javadoc, see interface
  public void leaveMessageBatch()
  {
    System.out.println(Catalog.CONSOLE_MSG_BATCH_LEAVE.lookup());
  }


  // non-javadoc, see interface
  public void visitMessage(String originator, String timestamp, String text)
  {
    System.out.println(Catalog.CONSOLE_MESSAGE_ABOUT_2.format(originator,
                                                              timestamp));
    System.out.println(text);
  }


  // non-javadoc, see interface
  public void visitTicketGrant(String ticket)
  {
    System.out.println(Catalog.CONSOLE_TICKET_1.format(ticket));
  }

}

