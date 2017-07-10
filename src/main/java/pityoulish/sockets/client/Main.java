/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.client;

import pityoulish.cmdline.ArgsInterpreter;
import pityoulish.cmdline.BackendHandler;
import pityoulish.mbclient.MsgBoardClientHandler;
import pityoulish.mbclient.MsgBoardCommandDispatcher;


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
    // rb and rp define the binary format, in this case TLV
    // They could be replaced by a JSON implementation, for example.
    RequestBuilder rb = new TLVRequestBuilderImpl();
    ResponseParser rp = new TLVResponseParserImpl(true);

    // The visitor encapsulates application logic for processing the response.
    // It doesn't know about the binary format, but about response elements.
    ResponseParser.Visitor rv = new FormattingVisitorImpl();

    // sbh connects to the backend, mbch sends requests and receives messages
    SocketBackendHandler   sbh = new SocketBackendHandlerImpl();
    MsgBoardClientHandler mbch =
      new MsgBoardClientHandlerImpl(sbh, rb, rp, rv, true);

    // mbcd interprets command-line arguments
    MsgBoardCommandDispatcher mbcd = new MsgBoardCommandDispatcher(mbch);
    ArgsInterpreter ai = new ArgsInterpreter(sbh, mbcd);

    int status = ai.handle(args);
    System.exit(status);
  }

}

