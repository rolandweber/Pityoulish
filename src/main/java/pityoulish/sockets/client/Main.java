/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import pityoulish.cmdline.ArgsInterpreter;
import pityoulish.cmdline.BackendHandler;


/**
 * Main entry point to the Message Board Client with sockets.
 */
public class Main
{
  /**
   * Main entry point.
   *
   * @param args        the command-line arguments
   */
  public final static void main(String[] args)
    throws Exception
  {
    RequestBuilder rb = new TLVRequestBuilderImpl();

    SocketBackendHandler sbh = new SocketBackendHandlerImpl();
    MsgBoardClientHandler mbch = new MsgBoardClientHandlerImpl(sbh, rb);

    MsgBoardCommandDispatcher mbcd = new MsgBoardCommandDispatcher(mbch);
    ArgsInterpreter ai = new ArgsInterpreter(sbh, mbcd);

    int status = ai.handle(args);
    System.exit(status);
  }

}

