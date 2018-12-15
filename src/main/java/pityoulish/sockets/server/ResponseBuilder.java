/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.nio.ByteBuffer;

import pityoulish.msgboard.MessageBatch;


/**
 * Builds binary response PDUs.
 * Each protocol data unit (PDU) is returned as a ByteBuffer.
 * The remaining content of that buffer is the PDU.
 */
public interface ResponseBuilder
{

  /**
   * Builds a response with an informational message.
   *
   * @param response    the response, holding either
   *                    an info or an error message
   *
   * @return a buffer containing the response PDU, backed by an array
   */
  public ByteBuffer buildInfoResponse(MsgBoardResponse<String> response)
    ;


  /**
   * Builds a response with an error message.
   *
   * @param msg   the error message
   *
   * @return a buffer containing the response PDU, backed by an array
   */
  public ByteBuffer buildErrorResponse(String msg)
    ;


  /**
   * Builds a response with an error message, from an exception.
   *
   * @param cause   the exception
   *
   * @return a buffer containing the response PDU, backed by an array
   */
  public ByteBuffer buildErrorResponse(Throwable cause)
    ;


  /**
   * Builds a response with a message batch.
   *
   * @param response    the response, holding either
   *                    a message batch or an error message
   *
   * @return a buffer containing the response PDU, backed by an array
   */
  public ByteBuffer buildMessageBatch(MsgBoardResponse<MessageBatch> response)
    ;


  /**
   * Builds a response with a ticket grant.
   *
   * @param response    the response, holding either ticket
   *                    {@link pityoulish.tickets.Ticket#getToken token}
   *                    or an error message
   *
   * @return a buffer containing the response PDU, backed by an array
   */
  public ByteBuffer buildTicketGrant(MsgBoardResponse<String> response)
    ;


}
