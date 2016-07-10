/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.msgboard;

import org.junit.*;
import static org.junit.Assert.*;


public class TimestamperImplTest
{
  @Test public void format_now()
  {
    Timestamper ts = new TimestamperImpl();

    String now = ts.getTimestamp();

    assertNotNull("no timestamp", now);
    assertEquals("wrong length", 20, now.length());
    assertEquals("wrong timezone", 'Z', now.charAt(19));
  }


  @Test public void format_sometime()
  {
    Timestamper ts = new TimestamperImpl();

    String then = ts.getTimestamp(1468132300600L);

    assertEquals("wrong timestamp", "2016-07-10T06:31:40Z", then);
  }


  @Test public void format_wayback()
  {
    Timestamper ts = new TimestamperImpl();

    String then = ts.getTimestamp(8L);

    assertEquals("wrong timestamp", "1970-01-01T00:00:00Z", then);
  }

}
