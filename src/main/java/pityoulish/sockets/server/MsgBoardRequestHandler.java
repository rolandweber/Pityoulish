/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.net.InetAddress;

import pityoulish.msgboard.MessageBatch;


/**
 * Handles {@link MsgBoardRequest}s.
 * This encapsulates application-level logic.
 * There is neither request parsing nor response building here.
 * Input is expected as an already parsed request object.
 * On success, output is provided as a single result object.
 * On error, an exception is thrown.
 *
 * <p>
 * Through this interface, the application logic is completely separated
 * from the format of requests and responses. For example, if you define
 * a protocol that uses JSON objects instead of TLVs to encode the same data,
 * you can re-use the application logic without changes.
 * On the other hand, you can replace the application logic
 * without touching the TLV (or JSON) processing.
 * </p>
 */
public interface MsgBoardRequestHandler
{
  /**
   * Lists messages from the board.
   *
   * @param mbreq       the request to
   *       {@link MsgBoardRequest.ReqType#LIST_MESSAGES LIST_MESSAGES}
   * @param address     the network address of the client
   *
   * @return the batch of listed messages
   *
   * @throws ProtocolException  in case of a problem
   */
  public MessageBatch listMessages(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
    ;

    
  /**
   * Puts a message on the board.
   *
   * @param mbreq       the request to
   *       {@link MsgBoardRequest.ReqType#PUT_MESSAGE PUT_MESSAGE}
   * @param address     the network address of the client
   *
   * @return an informational text
   *
   * @throws ProtocolException  in case of a problem
   */
  public String putMessage(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
    ;

    
  /**
   * Obtains a ticket for the board.
   *
   * @param mbreq       the request to
   *       {@link MsgBoardRequest.ReqType#OBTAIN_TICKET OBTAIN_TICKET}
   * @param address     the network address of the client
   *
   * @return the ticket token
   *
   * @throws ProtocolException  in case of a problem
   */
  public String obtainTicket(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
    ;

    
  /**
   * Returns a ticket for the board.
   *
   * @param mbreq       the request to
   *       {@link MsgBoardRequest.ReqType#RETURN_TICKET RETURN_TICKET}
   * @param address     the network address of the client
   *
   * @return an informational text
   *
   * @throws ProtocolException  in case of a problem
   */
  public String returnTicket(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
    ;

    
  /**
   * Replaces a ticket for the board.
   *
   * @param mbreq       the request to
   *       {@link MsgBoardRequest.ReqType#REPLACE_TICKET REPLACE_TICKET}
   * @param address     the network address of the client
   *
   * @return the ticket token
   *
   * @throws ProtocolException  in case of a problem
   */
  public String replaceTicket(MsgBoardRequest mbreq, InetAddress address)
    throws ProtocolException
    ;
    
}
