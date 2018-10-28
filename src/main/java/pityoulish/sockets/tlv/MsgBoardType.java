/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;


/**
 * TLV types of the binary protocol for Message Boards.
 * See the protocol specification for details.
 */
public enum MsgBoardType
 {
   // primitive TLV ===========================================================

   /**
    * A text.
    * The value of the TLV is a string in UTF-8 encoding.
    */
   TEXT((byte)0xC0),

   /**
    * A username or other originator.
    * The value of the TLV is a string of printable US-ASCII characters.
    */
   ORIGINATOR((byte)0xC1),

   /**
    * A timestamp.
    * The value of the TLV is a string of printable US-ASCII characters.
    */
   TIMESTAMP((byte)0xC2),

   /**
    * A marker.
    * The value of the TLV is a string of printable US-ASCII characters.
    */
   MARKER((byte)0xC3),

   /**
    * A batch size limit.
    * The value is a single byte with a value in the range 1 to 127, inclusive.
    */
   LIMIT((byte)0xC4),

   /**
    * An indicator for possibly missed messages.
    * The value of the TLV is empty.
    */
   MISSED((byte)0xC5),

   /**
    * A ticket.
    * The value of the TLV is a string of printable US-ASCII characters.
    */
   TICKET((byte)0xC6),


   // constructed TLV =========================================================

   /**
    * A message.
    * Comprises {@link #ORIGINATOR originator},
    * {@link #TIMESTAMP timestamp},
    * and {@link #TEXT text}.
    */
   MESSAGE((byte)0xE0),

   /**
    * Info response to a request.
    * The response contains a {@link #TEXT text}
    * and does not indicate an error.
    */
   INFO_RESPONSE((byte)0xE1),

   /**
    * Error response to a request.
    * The response contains a {@link #TEXT text} which describes the error.
    */
   ERROR_RESPONSE((byte)0xE2),


   /**
    * Request to obtain {@link #MESSAGE messages} from the board.
    * The request contains a {@link #LIMIT limit},
    * and may contain a {@link #MARKER marker}.
    * On success, the response is a {@link #MESSAGE_BATCH message batch}.
    */
   LIST_MESSAGES((byte)0xE3),

   /**
    * Response with a batch of messages.
    * Comprises a {@link #MARKER marker}, possibly a {@link #MISSED} indicator,
    * and a chronological sequence of {@link #MESSAGE messages}
    * which might be empty.
    */
   MESSAGE_BATCH((byte)0xE4),

   /**
    * Request to put a message on the board.
    * The request contains a {@link #TICKET ticket} and
    * the {@link #TEXT text} of the message to publish.
    * On success, an {@link #INFO_RESPONSE info} response is returned.
    */
   PUT_MESSAGE((byte)0xE5),

   /**
    * Request to obtain a {@link #TICKET ticket}.
    * The request contains an {@link #ORIGINATOR originator}
    * with the preferred username.
    * On success, a {@link #TICKET_GRANT ticket grant} is returned.
    */
   OBTAIN_TICKET((byte)0xE6),

   /**
    * Response with a granted {@link #TICKET ticket}.
    */
   TICKET_GRANT((byte)0xE7),

   /**
    * Request to return and invalidate a {@link #TICKET ticket}.
    * That is the only element of this request.
    * On success, an {@link #INFO_RESPONSE info} response is returned.
    */
   RETURN_TICKET((byte)0xE8),

   /**
    * Request to replace a {@link #TICKET ticket} with a new one.
    * That is the only element of this request.
    * On success, an {@link #INFO_RESPONSE info} response is returned.
    */
   REPLACE_TICKET((byte)0xE9)
   ;


   public final byte typeByte;
   private MsgBoardType(byte tb) { typeByte = tb; }

   /**
    * Obtains the <code>type</code> byte for this TLV type.
    *
    * @return the type, as a byte
    */
   public final byte getTypeByte() { return typeByte; }


   /**
    * Indicates whether this is a primitive TLV type.
    *
    * @return <code>true</code> if this is a primitive TLV type,
    *         <code>false</code> otherwise
    */
   public final boolean isPrimitive()
   {
     return (typeByte & 0xE0) == 0xC0;
   }

}
