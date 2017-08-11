/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

import java.net.InetAddress;

import org.junit.*;
import static org.junit.Assert.*;

import pityoulish.mbserver.StringProblemFactory;


public class DefaultTSanityCheckerTest
{
  public final static DefaultTSanityChecker<String> newChecker()
  {
    return new DefaultTSanityChecker<>(new StringProblemFactory());
  }


  @Test public void create_badArgs()
    throws Exception
  {
    final StringProblemFactory spf = new StringProblemFactory();
    final int uminlen = 2;
    final int umaxlen = 666;

    try {
      DefaultTSanityChecker<String> checker =
        new DefaultTSanityChecker<>(null);
      fail("missing problem factory not detected (1 arg): "+checker);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      DefaultTSanityChecker<String> checker =
        new DefaultTSanityChecker<>(null, uminlen, umaxlen, null);
      fail("missing problem factory not detected (4 args): "+checker);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      DefaultTSanityChecker<String> checker =
        new DefaultTSanityChecker<>(spf, -1, umaxlen, null);
      fail("invalid minimum length not detected: "+checker);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      DefaultTSanityChecker<String> checker =
        new DefaultTSanityChecker<>(spf, uminlen, -1, null);
      fail("invalid maximum length not detected: "+checker);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      DefaultTSanityChecker<String> checker =
        new DefaultTSanityChecker<>(spf, umaxlen, uminlen, null);
      fail("invalid min/max length not detected: "+checker);
    } catch (RuntimeException expected) {
      // expected
    }
  }


  @Test public void checkUsername_null()
    throws Exception
  {
    DefaultTSanityChecker<String> checker = newChecker();

    //@@@ TBD: exception or problem report when called with null? #11
    try {
      String problem = checker.checkUsername(null);
      fail("missing username not detected");
    } catch (RuntimeException expected) {
      // expected
    }
  }

  @Test public void checkUsername_empty()
    throws Exception
  {
    DefaultTSanityChecker<String> checker = newChecker();
    String problem = checker.checkUsername("");
    assertNotNull("missing username not detected", problem);
  }

  @Test public void checkUsername_short()
    throws Exception
  {
    DefaultTSanityChecker<String> checker = newChecker();
    String problem = checker.checkUsername("foo");
    assertNotNull("short username not detected", problem);
  }

  @Test public void checkUsername_long()
    throws Exception
  {
    DefaultTSanityChecker<String> checker = newChecker();
    String problem = checker.checkUsername("ThisIsAnInsanelyLongUsername");
    assertNotNull("long username not detected", problem);
  }

  @Test public void checkUsername_invalid()
    throws Exception
  {
    DefaultTSanityChecker<String> checker = newChecker();
    String problem = checker.checkUsername("user name");
    assertNotNull("whitespace in username not detected", problem);
  }

  @Test public void checkUsername_rw()
    throws Exception
  {
    DefaultTSanityChecker<String> checker = newChecker();
    // this one is explicitly allowed, for convenience when testing manually...
    String problem = checker.checkUsername("rw");
    assertNull("username 'rw' not allowed", problem);
  }

}
