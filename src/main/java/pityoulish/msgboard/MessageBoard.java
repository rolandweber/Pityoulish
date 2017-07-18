/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * A board with {@link Message messages} on it; unless it's empty.
 * The board maintains a collection of messages with chronological order.
 * The capacity is limited, so old messages eventually drop off the board
 * as new ones are added.
 * Methods to put messages on the board are defined in derived interfaces.
 */
public interface MessageBoard
{
  /**
   * Lists messages from this board.
   *
   * @param limit       the maximum number of messages to list,
   *                    0 or negative to list all available messages
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
   *            The discontinuation flag of the returned batch is set if a
   *            <code>marker</code> was given, but the board cannot ascertain
   *            that all messages since then have been retained.
   */
  public MessageBatch listMessages(int limit, String marker)
    ;

}
