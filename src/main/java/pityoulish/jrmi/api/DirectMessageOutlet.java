/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.api;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Endpoint for delivering direct messages.
 * A user has to create and {@link RemoteOutletManager publish}
 * an instance of this interface to receive direct messages.
 */
public interface DirectMessageOutlet extends Remote
{
  /**
   * Delivers a message to this outlet.
   *
   * @param originator  the purported originater of the message.
   *                    There is no way to authenticate the originator.
   * @param text        the content of the message
   *
   * @throws RemoteException    in case of an infrastructure problem
   */
  public void deliverMessage(String originator, String text)
    throws RemoteException
    ;


  /**
   * Does nothing.
   * Called to check if this outlet is still reachable by remote invocation.
   *
   * @throws RemoteException    in case of an infrastructure problem
   */
  public void ping()
    throws RemoteException
    ;

}
