/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;


/**
 * Useful constants for implementing the protocol.
 */
public final class ProtocolConstants
{
  /** Disabled constructor. */
  private ProtocolConstants()
  {
    throw new UnsupportedOperationException();
  }


  /**
   * The <i>type</i> values of the TLV structures used by the protocol.
   * TLV stands for <i>type - length - value</i>.
   * (Some people also use the term <i>tag</i> instead of <i>type</i>.)
   * <br/>
   * Constants with an underscore <code>_</code> in the name are for
   * complex types that contain other TLV structures. In particular,
   * these types are for requests and responses.
   * Constants without an underscore are for simple types that do not contain
   * other TLV structures. They are used within the request and response TLVs.
   */
  public enum TLVType
  {
    /**
     * Request to obtain a list of {@link #MESSAGE messages}.
     * A {@link #LIMIT limit} on the number of messages to return
     * is sent with the request.
     * This request does not require a ticket.
     * On success, a {@link #MESSAGE_LIST message list} is returned.
     */
    LIST_MESSAGES((byte)0xe0),

      /**
       * Request to obtain a {@link #TICKET ticket}.
       * A preferred {@link #USERNAME username} is sent with the request.
       * Of course, this request does not require a ticket.
       * On success, a {@link #TICKET_GRANT ticket grant} is returned.
       */
      OBTAIN_TICKET((byte)0xe1),

      /**
       * Request to store a {@link #MESSAGE message} on the server.
       * A valid {@link #TICKET ticket} and the message to publish
       * must be sent with the request.
       * On success, an {@link #INFO_RESPONSE info} response is returned.
       */
      PUT_MESSAGE((byte)0xe2),

      /**
       * Request to return and invalidate a {@link #TICKET ticket}.
       * A ticket must be sent with the request.
       * On success, an {@link #INFO_RESPONSE info} response is returned.
       */
      RETURN_TICKET((byte)0xe3),


      /**
       * Error response to a request.
       * The response contains an error {@link #MESSAGE message}.
       */
      ERROR_RESPONSE((byte)0xea),

      /**
       * Info response to a request.
       * The response contains a {@link #MESSAGE message}
       * and does not indicate an error.
       */
      INFO_RESPONSE((byte)0xeb),

      /**
       * List of {@link #MESSAGE messages} response.
       * The response can contain several messages.
       */
      MESSAGE_LIST((byte)0xec),

      /**
       * A {@link #TICKET} grant response.
       * The response contains a single ticket that is valid
       * at the time when the server generates the response.
       */
      TICKET_GRANT((byte)0xed),


      /**
       * A message text.
       * The value of the TLV is a string in UTF-8 encoding.
       * Note that the protocol does not allow arbitrary characters.
       * In particular, non-printing characters are excluded.
       */
      MESSAGE((byte)0xc0),

      /**
       * A username.
       * The value is a string in US-ASCII encoding.
       * Only alphanumeric characters (A-Za-z0-9) are allowed.
       */
      USERNAME((byte)0xc1),

      /**
       * A ticket.
       * The value of the TLV is an opaque series of bytes.
       * A server generates tickets, the clients copy them
       * into the subsequent requests.
       * <br/>
       * Each byte contains a printable ASCII character that is
       * neither whitespace nor expected to be a special character
       * in your average shell or command line interpreter.
       * This is to simplify copy&amp;paste on the command line.
       */
      TICKET((byte)0xc2),

      /**
       * A length limit.
       * The value is a single byte with a value in the range 0 to 127.
       */
      LIMIT((byte)0xc3)
      ;


    private final byte typeByte;
    private TLVType(byte rt) { typeByte = rt; }

    /** Obtains the <code>type</code> byte for this TLV structure type. */
    public final byte getTypeByte() { return typeByte; }
  };


  /**
   * A <i>length of length</i> value indicating 2 bytes for the length.
   * After the tag, the length of the TLV structure must be specified.
   * The protocol uses Basic Encoding Rules (BER) with this length-of-length
   * byte, followed by two bytes that hold the actual length.
   * The higher-order byte is stored before the lower-order byte.
   */
  public final static byte LENGTH_OF_LENGTH_2 = (byte)0x82;

}
