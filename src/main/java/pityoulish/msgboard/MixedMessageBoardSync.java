/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import pityoulish.mbserver.ProblemFactory;


/**
 * A synchronization wrapper for {@link MixedMessageBoard}.
 * This wrapper calls the underlying implementation in a thread-safe way.
 */
public class MixedMessageBoardSync implements MixedMessageBoard
{
  private final MixedMessageBoard board;


  /**
   * Creates a new, thread-safe message board wrapper.
   *
   * @param board   the underyling message board implementation,
   *                which is not required to be thread-safe
   */
  public MixedMessageBoardSync(MixedMessageBoard board)
  {
    if (board == null)
       throw new IllegalArgumentException("board is null");

    this.board = board;
  }


  // non-javadoc, see interface UserMessageBoard
  public synchronized
    <P> MSanityChecker<P> newSanityChecker(ProblemFactory<P> pf)
  {
    return board.newSanityChecker(pf);
  }


  // non-javadoc, see interface MessageBoard
  public synchronized MessageBatch listMessages(int limit, String marker)
  {
    return board.listMessages(limit, marker);
  }


  // non-javadoc, see interface UserMessageBoard
  public synchronized Message putMessage(String originator, String text)
  {
    return board.putMessage(originator, text);
  }


  // non-javadoc, see interface SystemMessageBoard
  public synchronized Message putSystemMessage(String slot, String text)
  {
    return board.putSystemMessage(slot, text);
  }


  // non-javadoc, see interface SystemMessageBoard
  public synchronized boolean removeSystemMessage(String slot)
  {
    return board.removeSystemMessage(slot);
  }


  // non-javadoc, see Object
  public synchronized String toString()
  {
    return "synchronized::" + board.toString();
  }

}

