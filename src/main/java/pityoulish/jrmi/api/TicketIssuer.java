/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.api;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Remote API for managing tickets.
 * On the caller side, tickets are represented by their token, a string.
 */
public interface TicketIssuer extends Remote
{
  /**
   * Obtains a ticket.
   *
   * @param username    the username for which to obtain the ticket
   *
   * @return    the token of a newly issued ticket for the given user.
   *            If the request is denied, an exception is thrown.
   *
   * @throws APIException       in case of an application-level problem
   * @throws RemoteException    in case of an infrastructure problem
   */
  public String obtainTicket(String username)
    throws RemoteException, APIException
    ;


  /**
   * Returns and invalidates a ticket.
   *
   * @param ticket   the {@link #obtainTicket token} of the ticket to return
   *
   * @throws APIException       in case of an application-level problem
   * @throws RemoteException    in case of an infrastructure problem
   */
  public void returnTicket(String ticket)
    throws RemoteException, APIException
    ;


  /**
   * Replaces a valid ticket.
   *
   * @param ticket   the {@link #obtainTicket token} of the ticket to replace
   *
   * @return    the token of a newly issued ticket for the same user.
   *            If the request is denied, an exception is thrown.
   *
   * @throws APIException       in case of an application-level problem
   * @throws RemoteException    in case of an infrastructure problem
   */
  public String replaceTicket(String ticket)
    throws RemoteException, APIException
    ;

}
