/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;


/**
 * Default implementation of {@link Message}.
 */
public class MessageImpl implements Message
{
  public final String originator;
  public final String timestamp;
  public final String text;


  /**
   * Creates a new message.
   * All attributes must be provided.
   * Their values are not checked though.
   *
   * @param originator   the originator
   * @param timestamp    the timestamp
   * @param text         the content
   */
  public MessageImpl(String originator, String timestamp, String text)
  {
    if (originator == null)
       throw new NullPointerException("originator");
    if (timestamp == null)
       throw new NullPointerException("timestamp");
    if (text == null)
       throw new NullPointerException("text");

    this.originator = originator;
    this.timestamp  = timestamp;
    this.text       = text;
  }


  // non-javadoc, see interface
  public final String getOriginator()
  {
    return originator;
  }


  // non-javadoc, see interface
  public final String getTimestamp()
  {
    return timestamp;
  }


  // non-javadoc, see interface
  public final String getText()
  {
    return text;
  }


  /**
   * Generates a human-readable description of this message.
   *
   * @return a representation of this message for debug output
   */
  public String toString()
  {
    int len = 2+originator.length()+timestamp.length()+text.length();
    StringBuilder sb = new StringBuilder(len);

    sb.append(originator).append(':')
      .append(timestamp).append(':')
      .append(text);

    return sb.toString();
  }

}
