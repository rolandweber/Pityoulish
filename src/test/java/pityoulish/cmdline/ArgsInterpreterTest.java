/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;

import org.junit.*;
import static org.junit.Assert.*;


public class ArgsInterpreterTest
{
  public static class MockBackendHandler implements BackendHandler
  {
    public int      numArgs;
    public String[] receivedArgs;

    /**
     * Creates a new mock backend handler.
     * If the last argument is "error", the handler throws an exception.
     */
    public MockBackendHandler(int argc)
    {
      numArgs = argc;
    }

    public void describeUsage(Appendable app, String cmd)
      throws java.io.IOException
    {
      app.append("Backend:").append(String.valueOf(numArgs))
        .append(' ').append(cmd).append('\n');
    }

    public int getArgCount()
    {
      return numArgs;
    }

    public void setBackend(String... args)
      throws Exception
    {
      receivedArgs = args;
      assertEquals("wrong number of backend arguments", numArgs, args.length);

      if ((args.length > 0) && "error".equalsIgnoreCase(args[args.length-1]))
         throw new Exception("MockBackendProblem");
    }

  } // class MockBackendHandler


  public static class MockCommandHandler implements CommandHandler
  {
    public String   commandName;
    public int      numArgs;
    public String[] receivedArgs;

    /**
     * Creates a new mock command handler.
     * The last argument determines the status on handling the command.
     * Pass a non-integer string argument to trigger an exception.
     * Commands without arguments always succeed with status 0.
     *
     * @param cmd       the name of the command to handle
     * @param argc      the exact number of arguments to handle.
     */
    public MockCommandHandler(String cmd, int argc)
    {
      commandName = cmd;
      numArgs = argc;
    }

    public void describeUsage(Appendable app)
      throws java.io.IOException
    {
      app.append("Command:").append(commandName)
        .append('#').append(String.valueOf(numArgs)).append('\n');
    }

    public boolean handle(StatusCode status, String name, String... args)
      throws Exception
    {
      if (!commandName.equalsIgnoreCase(name) || (numArgs != args.length))
         return false;

      receivedArgs = args;
      assertTrue("wrong command", commandName.equalsIgnoreCase(name));
      assertEquals("wrong number of arguments", numArgs, args.length);

      status.code = 0;
      if (args.length > 0)
         status.code = Integer.parseInt(args[args.length-1]);
      return true;
    }

  } // class MockCommandHandler


  /**
   * Creates an interpreter with a default set of handlers.
   * <ul>
   * <li>Backend handler with two arguments</li>
   * <li>Command handler for "none" without arguments</li>
   * <li>Command handler for "one" with one argument</li>
   * <li>Command handler for "two" with two arguments</li>
   * <li>Command handler for "one" with two arguments</li>
   * </ul>
   *
   * @return the argument interpreter
   */
  public static ArgsInterpreter createTestSubject()
  {
    MockBackendHandler mbh = new MockBackendHandler(2);
    MockCommandHandler ch0 = new MockCommandHandler("none", 0);
    MockCommandHandler ch1 = new MockCommandHandler("one", 1);
    MockCommandHandler ch2 = new MockCommandHandler("two", 2);
    MockCommandHandler ch12 = new MockCommandHandler("one", 2);

    return new ArgsInterpreter(mbh, ch0, ch1, ch2, ch12);
  }


  /**
   * Creates an interpreter with another default set of handlers.
   * <ul>
   * <li>Backend handler with no arguments</li>
   * <li>Command handler for "none" without arguments</li>
   * <li>Command handler for "one" with one argument</li>
   * <li>Command handler for "two" with two arguments</li>
   * <li>Command handler for "one" with two arguments</li>
   * </ul>
   *
   * @return the argument interpreter
   */
  public static ArgsInterpreter createAltTestSubject()
  {
    MockBackendHandler mbh = new MockBackendHandler(0);
    MockCommandHandler ch0 = new MockCommandHandler("none", 0);
    MockCommandHandler ch1 = new MockCommandHandler("one", 1);
    MockCommandHandler ch2 = new MockCommandHandler("two", 2);
    MockCommandHandler ch12 = new MockCommandHandler("one", 2);

    return new ArgsInterpreter(mbh, ch0, ch1, ch2, ch12);
  }


  /**
   * Creates an interpreter for a single command.
   * <ul>
   * <li>Backend handler with one argument</li>
   * <li>Command handler for "only" with one argument</li>
   * </ul>
   *
   * @return the argument interpreter
   */
  public static ArgsInterpreter createOnlyCommandTestSubject()
  {
    MockBackendHandler mbh = new MockBackendHandler(1);
    MockCommandHandler cho = new MockCommandHandler("fixed", 1);

    return new ArgsInterpreter(mbh, "fixed", cho);
  }


  /**
   * Asserts that the backend handler has been called with the expected args.
   *
   * @param args   the arguments for the backend, and possibly more
   * @param argc   the number of arguments expected by the backend.
   *            This number of arguments from the start of <code>args</code>
   *            will be verified.
   * @param mbh    the backend handler that should have been called
   */
  protected static void assertBackendArgs(String[] args, int argc,
                                          MockBackendHandler mbh)
  {
    assertNotNull("backend handler not called", mbh.receivedArgs);
    assertEquals("wrong number of backend args",
                 argc, mbh.receivedArgs.length);

    for (int i=0; i<argc; i++)
     {
       assertEquals("wrong backend arg #"+i, args[i], mbh.receivedArgs[i]);
     }
  }


  /**
   * Asserts that the command handler has been called with the expected args.
   *
   * @param args   the arguments for the command, and possibly more
   * @param argc   the number of arguments expected by the command.
   *            This number of arguments at the end of <code>args</code>
   *            will be verified.
   * @param mch    the command handler that should have been called
   */
  protected static void assertCommandArgs(String[] args, int argc,
                                          MockCommandHandler mch)
  {
    assertNotNull("expected command handler not called", mch.receivedArgs);
    assertEquals("wrong number of command args",
                 argc, mch.receivedArgs.length);

    for (int i=0; i<argc; i++)
     {
       assertEquals("wrong command arg #"+i,
                    args[i + args.length - argc], mch.receivedArgs[i]);
     }
  }


  @Test public void getUsage()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();

    String usage = ai.getUsage();

    System.out.println(usage);
    assertNotNull("no usage description", usage);
    assertTrue("missing usage for backend args",
               usage.indexOf("Backend:2") >= 0);
    assertTrue("missing usage for command none, no arg",
               usage.indexOf("Command:none#0") >= 0);
    assertTrue("missing usage for command one, one arg",
               usage.indexOf("Command:one#1") >= 0);
    assertTrue("missing usage for command two, two args",
               usage.indexOf("Command:two#2") >= 0);
    assertTrue("missing usage for command one, two args",
               usage.indexOf("Command:one#2") >= 0);
  }


  @Test public void handleNoArgs()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ };

    int status = ai.handle(args);

    assertFalse("unexpected success status", (0 == status));
  }


  @Test public void handleNoCommand()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "port" };

    int status = ai.handle(args);

    assertFalse("unexpected success status", (0 == status));
  }


  @Test public void handleUnknownCommand()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "port", "neverheardof" };

    int status = ai.handle(args);

    assertFalse("unexpected success status", (0 == status));
  }


  @Test public void handleTooFewArguments()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "port", "two", "1" };

    int status = ai.handle(args);

    assertFalse("unexpected success status", (0 == status));
  }


  @Test public void handleTooManyArguments()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "port", "two", "1", "2", "3" };

    int status = ai.handle(args);

    assertFalse("unexpected success status", (0 == status));
  }


  @Test public void handleNone_BackendProblem()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "error", "none" };

    try {
      int status = ai.handle(args);
      fail("exception not thrown, status="+status);
    } catch (Exception expected) {
      assertEquals("wrong exception message",
                   "MockBackendProblem", expected.getMessage());
    }
  }


  @Test public void handleNone_OK()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "port", "none" };

    int status = ai.handle(args);

    assertEquals("unexpected status", 0, status);
    assertBackendArgs(args, 2, (MockBackendHandler) ai.backendHandler);
    assertCommandArgs(args, 0, (MockCommandHandler) ai.cmdHandlers.get(0));
  }


  @Test public void handleNone_Alt_OK()
    throws Exception
  {
    ArgsInterpreter ai = createAltTestSubject();
    String[] args = new String[]{ "none" };

    int status = ai.handle(args);

    assertEquals("unexpected status", 0, status);
    assertBackendArgs(args, 0, (MockBackendHandler) ai.backendHandler);
    assertCommandArgs(args, 0, (MockCommandHandler) ai.cmdHandlers.get(0));
  }


  @Test public void handleOne_OK()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "port", "one", "111" };

    int status = ai.handle(args);

    assertEquals("unexpected status", 111, status);
    assertBackendArgs(args, 2, (MockBackendHandler) ai.backendHandler);
    assertCommandArgs(args, 1, (MockCommandHandler) ai.cmdHandlers.get(1));
  }


  @Test public void handleOne_Error()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "port", "one", "xyz" };

    try {
      int status = ai.handle(args);
      fail("exception not thrown, status="+status);
    } catch (NumberFormatException expected) {
      // expected
    }
  }


  @Test public void handleOne_Alt_OK()
    throws Exception
  {
    ArgsInterpreter ai = createAltTestSubject();
    String[] args = new String[]{ "one", "111" };

    int status = ai.handle(args);

    assertEquals("unexpected status", 111, status);
    assertBackendArgs(args, 0, (MockBackendHandler) ai.backendHandler);
    assertCommandArgs(args, 1, (MockCommandHandler) ai.cmdHandlers.get(1));
  }


  @Test public void handleTwo_OK()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "port", "two", "111", "222" };

    int status = ai.handle(args);

    assertEquals("unexpected status", 222, status);
    assertBackendArgs(args, 2, (MockBackendHandler) ai.backendHandler);
    assertCommandArgs(args, 2, (MockCommandHandler) ai.cmdHandlers.get(2));
  }


  @Test public void handleTwo_Alt_OK()
    throws Exception
  {
    ArgsInterpreter ai = createAltTestSubject();
    String[] args = new String[]{ "two", "111", "222" };

    int status = ai.handle(args);

    assertEquals("unexpected status", 222, status);
    assertBackendArgs(args, 0, (MockBackendHandler) ai.backendHandler);
    assertCommandArgs(args, 2, (MockCommandHandler) ai.cmdHandlers.get(2));
  }


  @Test public void handleOneTwo_OK()
    throws Exception
  {
    ArgsInterpreter ai = createTestSubject();
    String[] args = new String[]{ "server", "port", "one", "111", "112" };

    int status = ai.handle(args);

    assertEquals("unexpected status", 112, status);
    assertBackendArgs(args, 2, (MockBackendHandler) ai.backendHandler);
    assertCommandArgs(args, 2, (MockCommandHandler) ai.cmdHandlers.get(3));
  }


  @Test public void handleOneTwo_Alt_OK()
    throws Exception
  {
    ArgsInterpreter ai = createAltTestSubject();
    String[] args = new String[]{ "one", "111", "112" };

    int status = ai.handle(args);

    assertEquals("unexpected status", 112, status);
    assertBackendArgs(args, 0, (MockBackendHandler) ai.backendHandler);
    assertCommandArgs(args, 2, (MockCommandHandler) ai.cmdHandlers.get(3));
  }


  @Test public void handleOnlyCmd_OK()
    throws Exception
  {
    ArgsInterpreter ai = createOnlyCommandTestSubject();
    String[] args = new String[]{ "backend", "101" };

    int status = ai.handle(args);

    assertEquals("unexpected status", 101, status);
    assertBackendArgs(args, 1, (MockBackendHandler) ai.backendHandler);
    assertCommandArgs(args, 1, (MockCommandHandler) ai.cmdHandlers.get(0));
  }

}
