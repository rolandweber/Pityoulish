/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;


/**
 * Represents a request to the Message Board Server.
 *
 * Because the protocol is simple and has only a few possible data elements
 * in a request, it is feasible to define a single interface that represents
 * any kind of request. Depending on the request type, some of the getters
 * return values, while others return <code>null</code>.
 */
public interface MsgBoardRequest
{
  /**
   * The available request types.
   * They correspond to a subset of the
   * {@link pityoulish.sockets.tlv.MsgBoardType TLV types}.
   */
  public enum ReqType {
    LIST_MESSAGES, PUT_MESSAGE,
    OBTAIN_TICKET, RETURN_TICKET, REPLACE_TICKET;
  };


  /**
   * Obtains the request type.
   * This is the only attribute guaranteed to be present.
   *
   * @return    the request type, never <code>null</code>
   */
  public ReqType getReqType()
    ;


  public Integer getLimit()
    ;

  public String getMarker()
    ;

  public String getTicket()
    ;

  public String getText()
    ;

  public String getOriginator()
    ;

}
