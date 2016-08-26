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
     * Invoked for a ticket grant response.
     *
     * @param ticket    the ticket token
     */
    public void visitTicketGrant(String ticket)
      ;

    //@@@ methods for MessageBatch to be defined...
    //@@@ enterMessageBatch / leaveMessageBatch
    //@@@ visitMessage / visitMarker / visitMissed

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
