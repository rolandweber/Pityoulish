/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import java.nio.ByteBuffer;


/**
 * Suggested base class for implementing {@link MsgBoardClientHandler}.
 */
public abstract class MsgBoardClientHandlerBase
  implements MsgBoardClientHandler
{
  protected final SocketBackendHandler socketBackend;

  protected final RequestBuilder reqBuilder;

  protected final ResponseParser rspParser;

  protected final ResponseParser.Visitor rspVisitor;


  /**
   * Creates a new client handler implementation.
   *
   * @param sbh   the backend handler
   * @param rb    the request builder
   * @param rp    the response parser
   * @param rv    the response visitor
   */
  public MsgBoardClientHandlerBase(SocketBackendHandler sbh,
                                   RequestBuilder rb,
                                   ResponseParser rp,
                                   ResponseParser.Visitor rv)
  {
    if (sbh == null)
       throw new NullPointerException("SocketBackendHandler");
    if (rb == null)
       throw new NullPointerException("RequestBuilder");
    if (rp == null)
       throw new NullPointerException("ResponseParser");
    if (rv == null)
       throw new NullPointerException("ResponseParser.Visitor");

    socketBackend = sbh;
    reqBuilder = rb;
    rspParser = rp;
    rspVisitor = rv;
  }



  public void listMessages(int limit, String marker)
    throws Exception
  {
    ByteBuffer request = reqBuilder.buildListMessages(limit, marker);
    fireRequest(request);
  }


  public void putMessage(String ticket, String text)
    throws Exception
  {
    ByteBuffer request = reqBuilder.buildPutMessage(ticket, text);
    fireRequest(request);
  }


  public void obtainTicket(String username)
    throws Exception
  {
    ByteBuffer request = reqBuilder.buildObtainTicket(username);
    fireRequest(request);
  }


  public void returnTicket(String ticket)
    throws Exception
  {
    ByteBuffer request = reqBuilder.buildReturnTicket(ticket);
    fireRequest(request);
  }


  public void refreshTicket(String ticket)
    throws Exception
  {
    ByteBuffer request = reqBuilder.buildRefreshTicket(ticket);
    fireRequest(request);
  }



  /**
   * Sends a request and does whatever is necessary to processes the response.
   * It is not guaranteed that the response has been received and processed
   * by the time this method returns. That <i>can</i> be the case though.
   *
   * @param request     the request data to send
   *
   * @throws Exception   in case of a problem
   */
  protected abstract void fireRequest(ByteBuffer request)
    throws Exception
    ;

}
