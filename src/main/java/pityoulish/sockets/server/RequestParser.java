/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;


/**
 * Parses a binary request PDU into a {@link MsgBoardRequest}.
 */
// a generic return type would be possible, but is pointless here
public interface RequestParser
{
  /**
   * Parses a request TLV into a convenient in-memory representation.
   *
   * @param data    array containing the data to parse
   * @param start   index of the first byte of data to parse
   * @param end     index after the last byte of data to parse
   *
   * @return    the request information
   *
   * @throws ProtocolException  in case of invalid request data.
   *    Note that the parser performs only some basic validations.
   *    For example, it checks for missing TLV elements. However,
   *    it does not check a received text for valid characters or length.
   */
  public MsgBoardRequest parse(byte[] data, int start, int end)
    throws ProtocolException
    ;

}
