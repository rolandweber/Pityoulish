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
    //@@@ NLS light
    System.out.println("Info: "+text);
  }


  // non-javadoc, see interface
  public void visitError(String text)
  {
    //@@@ NLS light
    System.out.println("ERROR: "+text);
  }


  // non-javadoc, see interface
  public void enterMessageBatch(String marker, boolean missed)
  {
    //@@@ NLS light
    System.out.println(">>> MESSAGE BATCH");
    System.out.println("MARKER: "+marker);
    if (missed)
       System.out.println("Messages might have been missed.");
  }


  // non-javadoc, see interface
  public void leaveMessageBatch()
  {
    //@@@ NLS light
    System.out.println("<<< MESSAGE BATCH");
  }


  // non-javadoc, see interface
  public void visitMessage(String originator, String timestamp, String text)
  {
    //@@@ NLS light
    System.out.println("--- MESSAGE from '"+originator+"' at "+timestamp+": ");
    System.out.println(text);
  }


  // non-javadoc, see interface
  public void visitTicketGrant(String ticket)
  {
    //@@@ NLS light
    System.out.println("Ticket: "+ticket);
  }

}

