/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * Default implementation of {@link TypedMessage}.
 */
public class TypedMessageImpl<T extends Enum> extends MessageImpl
  implements TypedMessage
{
  public final T msgtype;


  /**
   * Creates a new message.
   * All attributes must be provided.
   * Their values are not checked though.
   *
   * @param originator   the originator
   * @param timestamp    the timestamp
   * @param text         the content
   * @param msgtype      the message type
   */
  public TypedMessageImpl(String originator, String timestamp, String text,
                          T msgtype)
  {
    super(originator, timestamp, text);

    if (msgtype == null)
       throw new NullPointerException("msgtype");

    this.msgtype = msgtype;
  }


  // non-javadoc, see interface
  public final T getType()
  {
    return msgtype;
  }


  //@@@ toString
}
