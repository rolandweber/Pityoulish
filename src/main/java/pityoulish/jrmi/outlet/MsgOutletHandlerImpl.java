/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

import pityoulish.jrmi.api.MessageList;
import pityoulish.jrmi.api.RemoteMessageBoard;
import pityoulish.jrmi.api.RemoteTicketIssuer;


/**
 * Default implementation of {@link MsgOutletHandler}.
 */
public class MsgOutletHandlerImpl
  implements MsgOutletHandler
{
  protected final RegistryBackendHandler regBackend;


  /**
   * Creates a new outlet handler implementation.
   *
   * @param rbh   the backend handler
   */
  public MsgOutletHandlerImpl(RegistryBackendHandler rbh)
  {
    if (rbh == null)
       throw new NullPointerException("RegistryBackendHandler");

    regBackend = rbh;
  }


  // non-javadoc, see interface MsgBoardClientHandler
  //@@@ methods to be defined

}
