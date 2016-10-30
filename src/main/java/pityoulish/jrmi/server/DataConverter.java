/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.server;

import java.util.ArrayList;
import java.util.List;

import pityoulish.msgboard.Message;
import pityoulish.msgboard.MessageBatch;

import pityoulish.jrmi.api.MessageItem;
import pityoulish.jrmi.api.MessageItemImpl;
import pityoulish.jrmi.api.MessageList;
import pityoulish.jrmi.api.MessageListImpl;


/**
 * A converter from internal server data to remote API data.
 * And in the other direction, but that isn't needed so far.
 * <p>
 * Here is the part of the code where it pays out that
 * the interfaces and classes in the remote API have different names
 * from those in the internal server API.
 * </p>
 */
public class DataConverter
{
  /** Disabled default constructor. */
  private DataConverter()
  {
    throw new UnsupportedOperationException("no instances of this class");
  }


  public static MessageList toMessageList(MessageBatch mb)
  {
    if (mb == null)
       throw new NullPointerException("MessageBatch");

    List<MessageItem> messages = new ArrayList<>(mb.getMessages().size());
    for(Message msg : mb.getMessages())
     {
       // convert a single message
       MessageItem mi = new MessageItemImpl(msg.getOriginator(),
                                            msg.getTimestamp(),
                                            msg.getText());
       messages.add(mi);
     }

    // convert the batch itself
    MessageList ml = new MessageListImpl(messages,
                                         mb.getMarker(),
                                         mb.isDiscontinuous());
    return ml;
  }

}
