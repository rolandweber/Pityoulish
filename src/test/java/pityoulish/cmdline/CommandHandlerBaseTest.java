/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;

import org.junit.*;
import static org.junit.Assert.*;


public class CommandHandlerBaseTest
{
  public enum TestCommand implements Command
  {
    NONE(0,0), ONE(1,1), SOME(2,6);

    public final int minArgs;
    public final int maxArgs;

    private TestCommand(int mina, int maxa)
     {
       minArgs = mina;
       maxArgs = maxa;
     }

    public final int getMinArgs() { return minArgs; }
    public final int getMaxArgs() { return maxArgs; }
  }


  /** Extend the abstract class to be tested. */
  public static class TestCmdHandler extends CommandHandlerBase<TestCommand>
  {
    public TestCommand receivedCommand;
    public String[]    receivedArgs;

    public TestCmdHandler()
     {
       super(TestCommand.class);
     }

    public void describeUsage(Appendable app)
     {
       // irrelevant
     }

    protected int handleCommand(TestCommand cmd, String... args)
      throws Exception
     {
       receivedCommand = cmd;
       receivedArgs = args;

       int status = 0;
       if (args.length > 0)
        {
          final String lastarg = args[args.length-1];
          if ("error".equalsIgnoreCase(lastarg))
             throw new Exception("MockCommandProblem");
          status = Integer.parseInt(lastarg);
        }

       return status;
     }

  } // class TestCmdHandler


  /**
   * Asserts that the expected command has been called with the expected args.
   *
   * @param cmd    the expected command
   * @param args   the expected arguments for the command
   * @param tch    the command handler that has been called
   */
  protected static void assertCommandAndArgs(TestCommand cmd,
                                             String[] args,
                                             TestCmdHandler tch)
  {
    assertEquals("wrong command", cmd, tch.receivedCommand);

    assertNotNull("missing command args", tch.receivedArgs);
    assertEquals("wrong number of command args",
                 args.length, tch.receivedArgs.length);

    for (int i=0; i<args.length; i++)
     {
       assertEquals("wrong command arg #"+i,
                    args[i], tch.receivedArgs[i]);
     }
  }



  @Test public void handleUnknown()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler();
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, "unknown", "arg1", "arg2");

    assertFalse("unsupported command has been handled", handled);
    assertEquals("status has been modified", 0, status.code);
  }


  @Test public void handleNone_OK()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler();
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, "none");

    assertTrue("command not handled", handled);
    assertCommandAndArgs(TestCommand.NONE, new String[0], tch);
    assertEquals("unexpected status", 0, status.code);
  }


  @Test public void handleOne_OK()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler();
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, "one", "111");

    assertTrue("command not handled", handled);
    assertCommandAndArgs(TestCommand.ONE, new String[]{ "111" }, tch);
    assertEquals("unexpected status", 111, status.code);
  }


  @Test public void handleOne_Error()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler();
    StatusCode  status = new StatusCode(0);

    try {
      boolean handled = tch.handle(status, "one", "error");
      fail("exception not thrown, handled="+handled+", status="+status.code);
    } catch (Exception expected) {
      assertEquals("wrong exception message",
                   "MockCommandProblem", expected.getMessage());
    }
  }


  @Test public void handleSome_OK()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler();
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, "some", "111", "222", "333");

    assertTrue("command not handled", handled);
    assertCommandAndArgs(TestCommand.SOME,
                         new String[]{ "111", "222", "333" },
                         tch);
    assertEquals("unexpected status", 333, status.code);
  }

}
