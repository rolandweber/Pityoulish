/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * A board for user messages.
 * User messages are stable. Once posted, they cannot be modified,
 * replaced, or removed until they drop off the board.
 * A batch which misses a user message is
 * {@link MessageBatch#isDiscontinuous discontinuous}.
 */
public interface UserMessageBoard extends MessageBoard
{
  /**
   * Puts a message on this board.
   * Boards have a limited capacity, so this might drop an old message.
   *
   * @param originator  the source of the message, a user or system name
   * @param text        the content of the message
   *
   * @return    the new message on the board, including a timestamp
   */
  public Message putMessage(String originator, String text)
    ;

}
