/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.nio.ByteBuffer;


/**
 * Parses binary response data.
 * Each element of the response is passed to a visitor for processing.
 */
public interface ResponseParser
{
  /**
   * Methods to be invoked for response data elements.
   */
  public static interface Visitor
  {
    /**
     * Invoked for an info response.
     *
     * @param text      the information
     */
    public void visitInfo(String text)
      ;

    /**
     * Invoked for an error response.
     *
     * @param text      the error description
     */
    public void visitError(String text)
      ;

    /**
     * Invoked at the beginning of a message batch response.
     * It is followed by zero or more calls to {@link #visitMessage},
     * and one call to {@link #leaveMessageBatch}.
     *
     * @param marker    the marker for requesting a subsequent batch
     * @param missed    <code>true</code> if messages might have been missed
     *                  since the last batch, <code>false</code> otherwise
     */
    public void enterMessageBatch(String marker, boolean missed)
      ;

    /**
     * Invoked at the end of a message batch response.
     * This indicates that there will be no more calls to {@link #visitMessage}
     * for the current batch. See also {@link #enterMessageBatch}.
     */
    public void leaveMessageBatch()
      ;

    /**
     * Invoked for each message in a batch.
     * See also {@link #enterMessageBatch} and {@link #leaveMessageBatch}.
     *
     * @param originator        the originator of the message
     * @param timestamp         the timestamp of the message
     * @param text              the contents of the message
     */
    public void visitMessage(String originator, String timestamp, String text)
      ;

    /**
     * Invoked for a ticket grant response.
     *
     * @param ticket    the ticket token
     */
    public void visitTicketGrant(String ticket)
      ;

  }; // interface Visitor


  /**
   * Parses a response.
   *
   * @param response    buffer holding the response to parse.
   *                    The buffer must be backed by an array.
   * @param visitor     the visitor to invoke on the response elements
   *
   * @throws Exception  in case of a problem
   */
  public void parse(ByteBuffer response, Visitor visitor)
    throws Exception
    ;

}
