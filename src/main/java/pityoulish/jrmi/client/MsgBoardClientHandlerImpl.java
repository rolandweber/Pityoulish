/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;


/**
 * Default implementation of {@link MsgBoardClientHandler}.
 */
public class MsgBoardClientHandlerImpl
  implements MsgBoardClientHandler
{
  protected final RegistryBackendHandler registryBackend;


  /**
   * Creates a new client handler implementation.
   *
   * @param rbh   the backend handler
   */
  //@@@ pass a call-completion handler (or output formatter? result handler?)
  public MsgBoardClientHandlerImpl(RegistryBackendHandler rbh)
  {
    if (rbh == null)
       throw new NullPointerException("RegistryBackendHandler");

    registryBackend = rbh;
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void listMessages(int limit, String marker)
    throws Exception
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void putMessage(String ticket, String text)
    throws Exception
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void obtainTicket(String username)
    throws Exception
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void returnTicket(String ticket)
    throws Exception
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void replaceTicket(String ticket)
    throws Exception
  {
    throw new UnsupportedOperationException("@@@ not yet implemented");
  }

}
