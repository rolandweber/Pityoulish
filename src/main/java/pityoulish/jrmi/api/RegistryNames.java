/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.api;


/**
 * Name constants for lookup in the RMI Registry.
 */
public final class RegistryNames
{
  /** Disabled default constructor. */
  private RegistryNames()
  {
    throw new UnsupportedOperationException("cannot be instantiated");
  }


  /** The name of the {@link RemoteMessageBoard} in the registry. */
  public final static String MESSAGE_BOARD = "RemoteMessageBoard";

  /** The name of the {@link TicketIssuer} in the registry. */
  public final static String TICKET_ISSUER = "TicketIssuer";

}
