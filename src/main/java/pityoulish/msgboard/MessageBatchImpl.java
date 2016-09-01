/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import java.util.List;


/**
 * Default implementation of {@link MessageBatch}.
 */
public class MessageBatchImpl implements MessageBatch
{
  public final List<? extends Message> messages;
  public final String  marker;
  public final boolean discontinuous;


  /**
   * Creates a batch of messages.
   * All attributes must be provided.
   *
   * @param messages        The list of messages in this batch, possibly empty.
   *                        This list is returned directly to callers of
   *                        {@link #getMessages}. Therefore, a read-only list
   *                        implementation should be passed to the constructor.
   * @param marker          The continuation marker for fetching subsequent
   *                        messages from the respective {@link MessageBoard}.
   * @param discontinuous   <code>true</code> if messages were lost since the
   *                        preceding batch, <code>false</code> otherwise.
   */
  public MessageBatchImpl(List<? extends Message> messages,
                          String marker,
                          boolean discontinuous)
  {
    if (messages == null)
       throw new NullPointerException("List of Message");
    if (marker == null)
       throw new NullPointerException("marker");

    this.messages      = messages;
    this.marker        = marker;
    this.discontinuous = discontinuous;
  }


  // non-javadoc, see interface
  public List<? extends Message> getMessages()
  {
    return messages;
  }


  // non-javadoc, see interface
  public String getMarker()
  {
    return marker;
  }


  // non-javadoc, see interface
  public boolean isDiscontinuous()
  {
    return discontinuous;
  }

}
