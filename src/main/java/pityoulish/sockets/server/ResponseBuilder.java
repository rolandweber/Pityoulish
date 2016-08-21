/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.msgboard.MessageBatch;


/**
 * Builds binary response PDUs.
 * Each protocol data unit (PDU) is returned as a byte array.
 * The PDU starts at index 0 and extends to the end of the array.
 */
public interface ResponseBuilder
{

  /**
   * Builds a response with an informational message.
   *
   * @param msg   the info message
   *
   * @return the response PDU
   */
  public byte[] buildInfoResponse(String msg)
    ;


  /**
   * Builds a response with an error message.
   *
   * @param msg   the error message
   *
   * @return the response PDU
   */
  public byte[] buildErrorResponse(String msg)
    ;


  /**
   * Builds a response with an error message, from an exception.
   *
   * @param cause   the exception
   *
   * @return the response PDU
   */
  public byte[] buildErrorResponse(Throwable cause)
    ;


  /**
   * Builds a response with a message batch.
   *
   * @param msgbatch   the message batch
   *
   * @return the response PDU
   */
  public byte[] buildMessageBatch(MessageBatch msgbatch)
    ;


  /**
   * Builds a response with a ticket grant.
   *
   * @param tictok  the ticket {@link pityoulish.tickets.Ticket#getToken token}
   *
   * @return the response PDU
   */
  public byte[] buildTicketGrant(String tictok)
    ;


}
