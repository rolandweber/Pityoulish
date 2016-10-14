/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.api;


/**
 * Indicates an application-level problem in the remote API.
 * Because this exception is part of a remote API, it must be serializable.
 * No problem, all exceptions are serializable.
 */
// intentionally not derived from RemoteException
public class APIException extends Exception
{
  // should be defined for every serializable class
  private final static long serialVersionUID = 20161014212626L;

  public APIException(String msg)
  {
    super(msg);
  }

  public APIException(String msg, Throwable cause)
  {
    super(msg, cause);
  }
}

