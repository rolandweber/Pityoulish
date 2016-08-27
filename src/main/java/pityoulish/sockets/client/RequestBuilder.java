/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.nio.ByteBuffer;


/**
 * Builds binary request PDUs.
 * Each protocol data unit (PDU) is returned as a ByteBuffer.
 * The remaining content of that buffer is the PDU.
 */
public interface RequestBuilder
{
  /**
   * Builds a request to list messages from the board.
   *
   * @param limit       the maximum batch size
   * @param marker      the marker where to start, or
   *                    <code>null</code> for the oldest available messages
   *
   * @return a buffer containing the request PDU, backed by an array
   */
  public ByteBuffer buildListMessages(int limit, String marker)
    ;


  /**
   * Builds a request to put a message on the board.
   *
   * @param ticket      the ticket that permits the operation
   * @param text        the text to put on the board
   *
   * @return a buffer containing the request PDU, backed by an array
   */
  public ByteBuffer buildPutMessage(String ticket, String text)
    ;


  /**
   * Builds a request to obtain a ticket for the board.
   *
   * @param username    the name for which to get the ticket.
   *                    It will appear as the originator of messages.
   *
   * @return a buffer containing the request PDU, backed by an array
   */
  public ByteBuffer buildObtainTicket(String username)
    ;


  /**
   * Builds a request to return a ticket for the board.
   *
   * @param ticket      the ticket to return
   *
   * @return a buffer containing the request PDU, backed by an array
   */
  public ByteBuffer buildReturnTicket(String ticket)
    ;


  /**
   * Builds a request to replace a ticket for the board.
   *
   * @param ticket      the ticket to replace
   *
   * @return a buffer containing the request PDU, backed by an array
   */
  public ByteBuffer buildReplaceTicket(String ticket)
    ;

}

