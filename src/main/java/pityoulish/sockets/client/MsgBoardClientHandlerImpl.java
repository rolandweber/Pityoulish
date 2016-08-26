/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.nio.ByteBuffer;


/**
 * Implementation of {@link MsgBoardClientHandler} with blocking IO.
 */
public class MsgBoardClientHandlerImpl extends MsgBoardClientHandlerBase
{

  /**
   * Creates a new client handler implementation with blocking IO.
   *
   * @param sbh   the backend handler
   * @param rb    the request builder
   */
  public MsgBoardClientHandlerImpl(SocketBackendHandler sbh, RequestBuilder rb)
  {
    super(sbh, rb);
  }



  /**
   * Sends a request and processes the response, with blocking IO.
   *
   * @param request     the request data to send
   *
   * @throws Exception   in case of a problem
   */
  protected void fireRequest(ByteBuffer request)
    throws Exception
  {
    // - connect to the backend
    // - send the request
    // - receive a response
    // - process the response
    throw new UnsupportedOperationException("@@@ should send request now");
  }

}
