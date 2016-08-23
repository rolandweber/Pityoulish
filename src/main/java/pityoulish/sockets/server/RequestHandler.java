/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;


/**
 * Handles requests, on the protocol level.
 * Expects binary request data as input,
 * provides binary response data as output.
 * Sending and receiving is left to the caller.
 * <br/>
 * Compare with {@link MsgBoardRequestHandler}, which
 * handles requests on the application level.
 */
public interface RequestHandler
{
  /**
   * Handles a binary request.
   * Processing problems are reported as error responses.
   *
   * @param reqdata   the request data
   * @param start     index of the first byte of the request data
   * @param end       index after the last byte of the request data
   *
   * @return          the response data
   */
  public byte[] handle(byte[] reqdata, int start, int end)
  // intentionally no exception declared here
    ;


  /**
   * Builds a response with an error message.
   * See also {@link ResponseBuilder#buildErrorResponse(String)}.
   *
   * @param msg   the error message
   *
   * @return the response data
   */
  public byte[] buildErrorResponse(String msg)
    ;


  /**
   * Builds a response with an error message, from an exception.
   * See also {@link ResponseBuilder#buildErrorResponse(Throwable)}.
   *
   * @param cause   the exception
   *
   * @return the response data
   */
  public byte[] buildErrorResponse(Throwable cause)
    ;

}
