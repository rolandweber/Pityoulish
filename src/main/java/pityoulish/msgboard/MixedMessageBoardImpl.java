/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import java.util.NavigableMap;
import java.util.TreeMap;


/**
 * An implementation of {@link MixedMessageBoard}.
 * This implementation is intended for dozens of messages.
 * To handle thousands of messages, you'd probably use other data structures.
 */
public class MixedMessageBoardImpl implements MixedMessageBoard
{
  /** The message types distinguished by this board. */
  public enum MT { USER, SYSTEM };


  /** The number of messages that fit on this board. */
  protected int boardCapacity;

  /** The sequencer for generating message IDs. */
  protected final Sequencer boardSequencer;

  /**
   * The ordered map of messages on this board.
   * Keys are the message IDs generated by the {@link #boardSequencer}.
   * Values are the actual messages.
   */
  protected NavigableMap<String,TypedMessage<MT>> boardMessages;

  /** ID of the last dropped user message. */
  protected String lastDroppedUserMessageID;


  /**
   * Creates a new message board.
   *
   * @param capacity    the number of messages that fit on the new board
   */
  public MixedMessageBoardImpl(int capacity)
  {
    if (capacity < 1)
       throw new IllegalArgumentException("capacity " + capacity);

    boardCapacity = capacity;
    boardSequencer = newSequencer();
    boardMessages = new TreeMap<>(boardSequencer.getComparator());
    lastDroppedUserMessageID = boardSequencer.createMessageID(); // dummy ID
  }


  /**
   * Creates the sequencer for this board.
   * Called by the constructor.
   * Override this method to use a different sequencer implementation.
   */
  protected Sequencer newSequencer()
  {
    return new TrivialSequencerImpl();
  }



  // non-javadoc, see interface MessageBoard
  public MessageBatch listMessages(int limit, String marker)
  {
    if (limit < boardMessages.size())
       throw new UnsupportedOperationException("@@@ limit not yet supported");
    if (marker != null)
       throw new UnsupportedOperationException("@@@ marker not yet supported");

    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface UserMessageBoard
  public Message putMessage(String originator, String text)
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface SystemMessageBoard
  public Message putSystemMessage(String slot, String text)
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface SystemMessageBoard
  public boolean removeSystemMessage(String slot)
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }

}
