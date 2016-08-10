/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.tlv;


/**
 * Represents a TLV structure to be parsed.
 */
public interface ParseTLV<T> extends TLV<T>
{
  // anything useful that could be added here?
  // public ParseTLV getContainedTLV() for the value?
  // public ParseTLV getFollowingTLV() for the next TLV?
  // How to determine if there is a next TLV? pass end position?
}
