/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.InetAddress;

import pityoulish.msgboard.MessageBatch;


/**
 * A helper to report selected events and data, for example on the console.
 * For use by {@link RequestHandlerImpl} or similar classes.
 */
public interface Expositor
{
  /**
   * Describe an incoming request.
   *
   * @param mbreq       the request to describe
   * @param address     the network address of the client
   */
  public void describeRequest(MsgBoardRequest mbreq, InetAddress address)
    ;


  /**
   * Describe an outgoing message batch.
   *
   * @param response    the response to describe,
   *                    either message batch or error
   */
  public void describeMessageBatch(MsgBoardResponse<MessageBatch> response)
    ;


  /**
   * Describe a ticket grant response.
   *
   * @param response    the response to describe,
   *                    either ticket grant or error
   */
  public void describeTicketGrant(MsgBoardResponse<String> response)
    ;


  /**
   * Describe an informational response.
   *
   * @param response    the response to describe,
   *                    either informational message or error
   */
  public void describeInfoResponse(MsgBoardResponse<String> response)
    ;


  /**
   * Describe an exception.
   *
   * @param axe   the exception or other throwable
   */
  public void describeException(Throwable axe)
    ;

}

