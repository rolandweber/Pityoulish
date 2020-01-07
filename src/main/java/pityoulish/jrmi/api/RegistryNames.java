/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.api;


/**
 * Name constants for lookup in the RMI Registry.
 */
public enum RegistryNames
{
  /** The name of the {@link RemoteMessageBoard} in the registry. */
  MESSAGE_BOARD("RemoteMessageBoard"),

  /** The name of the {@link RemoteTicketIssuer} in the registry. */
  TICKET_ISSUER("RemoteTicketIssuer");


  /** The name for looking up or registering this remote object. */
  public final String lookupName;


  private RegistryNames(String ln)
  {
    lookupName = ln;
  }


  public final String getLookupName()
  {
    return lookupName;
  }

}
