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
   * @param msg   the info message
   *
   * @return a buffer containing the response PDU, backed by an array
   */
  public ByteBuffer buildInfoResponse(String msg)
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
   * @param msgbatch   the message batch
   *
   * @return a buffer containing the response PDU, backed by an array
   */
  public ByteBuffer buildMessageBatch(MessageBatch msgbatch)
    ;


  /**
   * Builds a response with a ticket grant.
   *
   * @param tictok  the ticket {@link pityoulish.tickets.Ticket#getToken token}
   *
   * @return a buffer containing the response PDU, backed by an array
   */
  public ByteBuffer buildTicketGrant(String tictok)
    ;


}
