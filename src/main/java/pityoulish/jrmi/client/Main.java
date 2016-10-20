/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;

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
    // rbh deals with the registry, mbch calls the server remotely
    RegistryBackendHandler rbh = new RegistryBackendHandlerImpl();
    MsgBoardClientHandler mbch = new MsgBoardClientHandlerImpl(rbh);

    // mbcd interprets command-line arguments
    MsgBoardCommandDispatcher mbcd = new MsgBoardCommandDispatcher(mbch);
    ArgsInterpreter ai = new ArgsInterpreter(rbh, mbcd);

    int status = ai.handle(args);
    System.exit(status);
  }

}

