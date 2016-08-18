/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;


/**
 * Indicates a protocol violation.
 */
public class ProtocolException extends Exception
{
  public ProtocolException(String msg)
  {
    super(msg);
  }

  public ProtocolException(String msg, Throwable cause)
  {
    super(msg, cause);
  }
}

