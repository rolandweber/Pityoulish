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
  @Test public void dummy()
  {
    System.out.println("@@@ dummy test executed");
  }


  @Ignore @Test public void failure()
  {
    fail("@@@ dummy test failure");
  }

  @Ignore @Test public void error()
  {
    throw new Error("@@@ dummy test error");
  }
}
