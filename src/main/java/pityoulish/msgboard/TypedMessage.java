/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * A {@link Message} with a type.
 * Can be used to distinguish between user and system messages, for example.
 */
public interface TypedMessage<T extends Enum> extends Message
{
  /**
   * Obtains the type of this message.
   *
   * @return    the message type, never <code>null</code>
   */
  public T getType()
    ;

}
