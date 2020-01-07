/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.api.dm;

import java.util.List;

import java.rmi.Remote;
import java.rmi.RemoteException;

import pityoulish.jrmi.api.APIException;


/**
 * Remote interface for publishing {@link DirectMessageOutlet} instances.
 * Tickets must be obtained from the
 * {@link pityoulish.jrmi.api.RemoteTicketIssuer}.
 */
public interface RemoteOutletManager extends Remote
{
  /** The default name of this manager in the RMI registry. */
  public final static String REGISTRY_NAME = "RemoteOutletManager";


  /**
   * Publishes an outlet for direct messages.
   *
   * @param ticket      the ticket (token) to authorize the operation
   * @param outlet      the direct message endpoint to publish
   *
   * @throws APIException       in case of an application-level problem
   * @throws RemoteException    in case of an infrastructure problem
   */
  public void publishOutlet(String ticket, DirectMessageOutlet outlet)
    throws RemoteException, APIException
    ;


  /**
   * Unpublishes an outlet for direct messages.
   *
   * @param ticket      the ticket (token) to authorize the operation
   *
   * @throws APIException       in case of an application-level problem
   * @throws RemoteException    in case of an infrastructure problem
   */
  public void unpublishOutlet(String ticket)
    throws RemoteException, APIException
    ;


  /**
   * Obtains the usernames for which an outlet is published.
   *
   * @return  a list of usernames, possibly empty, never <code>null</code>
   *
   * @throws RemoteException    in case of an infrastructure problem
   */
  public List<String> listUsernames()
    throws RemoteException
    ;


  /**
   * Obtains the published outlet for a username.
   *
   * @param username    the username for which to get the outlet
   *
   * @return  the outlet for the username, never <code>null</code>.
   *          If there is no published outlet for the username,
   *          an exception will be thrown.
   *
   * @throws APIException       in case of an application-level problem
   * @throws RemoteException    in case of an infrastructure problem
   */
  public DirectMessageOutlet getOutlet(String username)
    throws RemoteException, APIException
    ;

}
