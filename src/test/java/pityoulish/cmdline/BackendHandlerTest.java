/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;

import org.junit.*;
import static org.junit.Assert.*;


public class BackendHandlerTest
{
  @Test public void describeUsage()
    throws Exception
  {
    String prefix = "abcdefg\n";
    String cmd = "xyz arg1 arg2";
    BackendHandler bh = new BackendHandler.None();

    StringBuilder sb = new StringBuilder(107);
    sb.append(prefix);
    bh.describeUsage(sb, "\n", cmd);

    String usage = sb.toString();
    assertTrue("usage prefix modified", usage.startsWith(prefix));
    assertTrue("missing command usage", (usage.indexOf(cmd) >= 0));
  }


  @Test public void getArgCount()
    throws Exception
  {
    BackendHandler bh = new BackendHandler.None();

    int argc = bh.getArgCount();

    assertEquals("wrong argument count", 0, argc);
  }


  @Test public void setBackend()
    throws Exception
  {
    BackendHandler bh = new BackendHandler.None();

    bh.setBackend();

    // nothing to assert, just making sure there is no exception
  }

}