/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import pityoulish.mbserver.ProblemFactory;


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
   * Creates a sanity checker for this message board.
   * The returned sanity checker is thread safe if the problem factory is.
   *
   * @param pf   the problem factory to be used by the sanity checker
   * @param <P>  the class of problems to be reported
   *
   * @return a sanity checker for this message board
   *         which uses the argument factory to report problems
   */
  public <P> MSanityChecker<P> newSanityChecker(ProblemFactory<P> pf)
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
