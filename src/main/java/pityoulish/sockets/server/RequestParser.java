/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import java.nio.ByteBuffer;


/**
 * Parses a binary request PDU into a {@link MsgBoardRequest}.
 */
// a generic return type would be possible, but is pointless here
public interface RequestParser
{
  /**
   * Parses a request TLV into a convenient in-memory representation.
   *
   * @param request   buffer holding the request to parse.
   *                  The buffer must be backed by an array.
   *
   * @return    the request information
   *
   * @throws ProtocolException  in case of invalid request data.
   *    Note that the parser performs only some basic validations.
   *    For example, it checks for missing TLV elements. However,
   *    it does not check a received text for valid characters or length.
   */
  public MsgBoardRequest parse(ByteBuffer request)
    throws ProtocolException
    ;

}
