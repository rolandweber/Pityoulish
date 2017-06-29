/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;

import org.junit.*;
import static org.junit.Assert.*;


public class SingleCommandHandlerBaseTest
{
  /** Extend the abstract class to be tested. */
  public static class TestCmdHandler extends SingleCommandHandlerBase
  {
    public String[] receivedArgs;

    public TestCmdHandler(int mina, int maxa)
     {
       super(mina, maxa);
     }

    public void describeUsage(Appendable app)
     {
       // irrelevant
     }

    protected int handleCommand(String... args)
      throws Exception
     {
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
   * Asserts that the command has been called with the expected args.
   *
   * @param args   the expected arguments for the command
   * @param tch    the command handler that has been called
   */
  protected static void assertCommandArgs(String[] args,
                                          TestCmdHandler tch)
  {
    assertNotNull("missing command args", tch.receivedArgs);
    assertEquals("wrong number of command args",
                 args.length, tch.receivedArgs.length);

    for (int i=0; i<args.length; i++)
     {
       assertEquals("wrong command arg #"+i,
                    args[i], tch.receivedArgs[i]);
     }
  }


  @Test public void constructor_BadArgs()
    throws Exception
  {
    try {
      TestCmdHandler tch = new TestCmdHandler(-1, 1);
      fail("negative minArgs not detected");
    } catch (RuntimeException expected) {
    }

    try {
      TestCmdHandler tch = new TestCmdHandler(2, 1);
      fail("minArgs > maxArgs not detected");
    } catch (RuntimeException expected) {
    }

    // the good case
    TestCmdHandler tch = new TestCmdHandler(0, 0);
  }


  @Test public void handleUnknown()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler(0, 0);
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, "not"+tch.getCommandName());

    assertFalse("unsupported command has been handled", handled);
    assertEquals("status has been modified", 0, status.code);
  }


  @Test public void handleTooFew()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler(2, 2);
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, tch.getCommandName(), "111");

    assertFalse("command has been handled", handled);
    assertEquals("status has been modified", 0, status.code);
  }


  @Test public void handleTooMany()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler(0, 1);
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, tch.getCommandName(), "111", "222");

    assertFalse("command has been handled", handled);
    assertEquals("status has been modified", 0, status.code);
  }


  @Test public void handleNone_OK()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler(0, 2);
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, tch.getCommandName());

    assertTrue("command not handled", handled);
    assertCommandArgs(new String[0], tch);
    assertEquals("unexpected status", 0, status.code);
  }


  @Test public void handleOne_OK()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler(0, 2);
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, tch.getCommandName(), "111");

    assertTrue("command not handled", handled);
    assertCommandArgs(new String[]{ "111" }, tch);
    assertEquals("unexpected status", 111, status.code);
  }


  @Test public void handleOne_Error()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler(0, 2);
    StatusCode  status = new StatusCode(0);

    try {
      boolean handled = tch.handle(status, tch.getCommandName(), "error");
      fail("exception not thrown, handled="+handled+", status="+status.code);
    } catch (Exception expected) {
      assertEquals("wrong exception message",
                   "MockCommandProblem", expected.getMessage());
    }
  }


  @Test public void handleSome_OK()
    throws Exception
  {
    TestCmdHandler tch = new TestCmdHandler(0, 2);
    StatusCode  status = new StatusCode(0);

    boolean handled = tch.handle(status, tch.getCommandName(), "111", "222");

    assertTrue("command not handled", handled);
    assertCommandArgs(new String[]{ "111", "222" },
                      tch);
    assertEquals("unexpected status", 222, status.code);
  }

}

