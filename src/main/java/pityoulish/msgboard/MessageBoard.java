/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * A message board, with {@link Message messages} on it.
 * Unless it's empty.
 */
public interface MessageBoard
{
  /**
   * Lists messages from this board.
   *
   * @param limit       the maximum number of messages to list
   * @param marker      the continuation {@link MessageBatch#getMarker marker}
   *                    from a preceding call, or
   *                    <code>null</code> to fetch the oldest messages
   *                    from this board
   *
   * @return    the oldest available messages that are newer than the
   *            <code>marker</code>.
   *            The order of the messages is from older to newer.
   *            The batch contains no more than <code>limit</code> messages.
   *            The continuation marker of the returned batch can be used to
   *            fetch only newer messages on subsequent calls.
   *            The discontinuation flag is set on the returned batch if a
   *            <code>marker</code> was given, but the board cannot ascertain
   *            that all messages since then have been retained.
   */
  public MessageBatch listMessages(int limit, String marker)
    ;


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
