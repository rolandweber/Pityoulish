/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;


/**
 * Available actions for the Message Outlet program.
 * Called by the {@link MsgOutletCommandDispatcher},
 * with arguments obtained from the command line.
 */
public interface MsgOutletHandler
{
  /**
   * Lists the usernames for which an outlet is published.
   */
  public void listUsernames()
    throws Exception
    ;


  /**
   * Opens an outlet for a while and publishes it.
   * The outlet will close automatically, but will not be unpublished.
   * The ticket might be expired by then anyway.
   *
   * @param ticket      the ticket that permits the operation
   * @param seconds     how long to keep open
   */
  public void openOutlet(String ticket, int seconds)
    throws Exception
    ;


  /**
   * Sends a message to receivers.
   *
   * @param originator   the (purported) originator of the message
   * @param text         the content of the message
   * @param recipients   the recipients of the message,
   *                     "*" for all published outlets
   */
  public void sendMessage(String originator, String text, String... recipients)
    throws Exception
    ;


  /**
   * Unpublishes an outlet.
   * The username is implicitly specified by the ticket.
   *
   * @param ticket      the ticket that permits the operation
   */
  public void unpublishOutlet(String ticket)
    throws Exception
    ;

}
