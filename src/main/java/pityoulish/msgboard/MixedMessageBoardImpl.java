/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

  /**
   * Helper class to eliminate type parameters from the message implementation.
   * Don't know how else to get rid of "unchecked conversion" warnings.
   * But it makes the code more readable anyway, by eliminating nested
   * type parameters from the collection.
   */
  public static class MTMsg extends TypedMessageImpl<MT>
  {
    public MTMsg(String originator, String timestamp, String text, MT msgtype)
    {
      super(originator, timestamp, text, msgtype);
    }
  }


  /** The number of messages that fit on this board. */
  protected int boardCapacity;

  /** The sequencer for generating message IDs. */
  protected final Sequencer boardSequencer;

  /** The timestamp generator. */
  protected Timestamper boardTimer;

  /**
   * The ordered map of messages on this board.
   * Keys are the message IDs generated by the {@link #boardSequencer}.
   * Values are the actual messages.
   */
  protected NavigableMap<String,MTMsg> boardMessages;

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

    boardCapacity  = capacity;
    boardSequencer = newSequencer();
    boardTimer     = newTimestamper();
    boardMessages  = new TreeMap<>(boardSequencer.getComparator());
    lastDroppedUserMessageID = boardSequencer.createMessageID(); // dummy ID
  }


  /**
   * Creates the sequencer for this board.
   * Called by the constructor.
   * Override this method to use a different sequencer implementation.
   */
  protected Sequencer newSequencer()
  {
    return new SimpleSequencerImpl();
  }


  /**
   * Creates the timestamp generator for this board.
   * Called by the constructor.
   * Override this method to use a different timestamp format.
   */
  protected Timestamper newTimestamper()
  {
    return new TimestamperImpl();
  }



  // non-javadoc, see interface MessageBoard
  public MessageBatch listMessages(int limit, String marker)
  {
    if (limit < 1)
       throw new IllegalArgumentException("limit "+limit);
    if ((marker != null) && !boardSequencer.isSane(marker))
       throw new IllegalArgumentException("invalid marker '"+marker+"'");

    Iterator<Map.Entry<String,MTMsg>> iter = null;
    int     count = 0;
    boolean discontinuous = false;

    if (marker == null)
     {
       // list oldest messages
       iter  = boardMessages.entrySet().iterator();
       count = Math.min(boardMessages.size(), limit);
     }
    else
     {
       // list messages newer than marker
       NavigableMap<String,MTMsg>
         newerMessages = boardMessages.tailMap(marker, false);
       count = Math.min(newerMessages.size(), limit);
       iter = newerMessages.entrySet().iterator();
       discontinuous = (boardSequencer.getComparator().
                        compare(lastDroppedUserMessageID, marker) > 0);
     }

    List<MTMsg> messages = new ArrayList<>(count);
    String    tailMarker = null; // becomes the marker in the result

    while ((count > 0) && iter.hasNext())
     {
       Map.Entry<String,MTMsg> entry = iter.next();
       messages.add(entry.getValue());
       tailMarker = entry.getKey();
       count--;
     }

    if (tailMarker == null) // no messages since marker
       tailMarker = marker;

    if (tailMarker == null) // board still empty
       tailMarker = lastDroppedUserMessageID;

    return new MessageBatchImpl
      (Collections.<Message> unmodifiableList(messages),
       tailMarker, discontinuous);
  }


  // non-javadoc, see interface UserMessageBoard
  public Message putMessage(String originator, String text)
  {
    MTMsg msg = new MTMsg
      (originator, boardTimer.getTimestamp(), text, MT.USER);

    addMessageToBoard(msg);

    return msg;
  }


  // non-javadoc, see interface SystemMessageBoard
  public Message putSystemMessage(String slot, String text)
  {
    String originator = "_";
    if (slot != null)
     {
       originator = "_#"+slot;
       //@@@ if there is a message for the slot, remove it now
       throw new UnsupportedOperationException("@@@ slot not yet supported");
     }

    MTMsg msg = new MTMsg
      (originator, boardTimer.getTimestamp(), text, MT.SYSTEM);

    addMessageToBoard(msg);

    return msg;
  }


  // non-javadoc, see interface SystemMessageBoard
  public boolean removeSystemMessage(String slot)
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  /**
   * Adds a message to the board.
   * This might drop an old message. All book-keeping is taken care of here.
   *
   * @param msg   the message to add
   *
   * @return      ID of the added message
   */
  protected String addMessageToBoard(MTMsg msg)
  {
    if (msg == null)
       throw new NullPointerException("TypedMessage");

    String key = boardSequencer.createMessageID();
    boardMessages.put(key, msg);

    // if the capacity remains constant, at most one message will be dropped
    while (boardMessages.size() > boardCapacity)
     {
       Map.Entry<String,MTMsg> entry = boardMessages.pollFirstEntry();
       if (entry.getValue().getType() == MT.USER)
          lastDroppedUserMessageID = entry.getKey();
     }

    return key;
  }

}

