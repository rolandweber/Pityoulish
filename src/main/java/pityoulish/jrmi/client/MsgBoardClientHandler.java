/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;


/**
 * Available actions for the Message Board client.
 * Called by the {@link MsgBoardCommandDispatcher},
 * with arguments obtained from the command line.
 */
public interface MsgBoardClientHandler
{
  /**
   * Lists messages from the board.
   *
   * @param limit       the maximum batch size
   * @param marker      the marker where to start, or
   *                    <code>null</code> for the oldest available messages
   */
  public void listMessages(int limit, String marker)
    throws Exception
    ;


  /**
   * Puts a message on the board.
   *
   * @param ticket      the ticket that permits the operation
   * @param text        the text to put on the board
   */
  public void putMessage(String ticket, String text)
    throws Exception
    ;


  /**
   * Obtains a ticket for the board.
   *
   * @param username    the name for which to get the ticket.
   *                    It will appear as the originator of messages.
   */
  public void obtainTicket(String username)
    throws Exception
    ;


  /**
   * Returns a ticket for the board.
   *
   * @param ticket      the ticket to return
   */
  public void returnTicket(String ticket)
    throws Exception
    ;


  /**
   * Replaces a ticket for the board.
   *
   * @param ticket      the ticket to replace
   */
  public void replaceTicket(String ticket)
    throws Exception
    ;

}
