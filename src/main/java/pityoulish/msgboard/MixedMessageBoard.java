/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * A board for both user and system messages.
 * Mixing different types of messages brings some fun into the exercise.
 * Each caller that periodically fetches new messages from the board
 * sees the same stream of user messages, but interspersed with
 * possibly different system messages.
 * <br/>
 * A batch that misses a user message is discontinuous.
 * A batch that misses a system message which has been updated or removed
 * is still continuous.
 * A batch that misses a system message which dropped off the board without
 * being updated or removed may or may not be considered discontinuous;
 * that is implementation dependent. Anyway, keep in mind that
 * {@link MessageBatch#isDiscontinuous MessageBatch.isDiscontinuous()}
 * may be set even if the batch is not missing any message.
 * The method specification allows for best-effort implementations
 * that err on the safe side.
 */
public interface MixedMessageBoard extends UserMessageBoard, SystemMessageBoard
{
  // no extra methods
}

