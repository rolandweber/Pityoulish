/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

import java.net.InetAddress;

import org.junit.*;
import static org.junit.Assert.*;


public class DefaultTicketManagerTest
{
  public static class TicketWrapper implements Ticket
  {
    public final Ticket wrapped;

    public TicketWrapper(Ticket t)
    {
      wrapped = t;
    }

    public String  getUsername() { return wrapped.getUsername(); }
    public String  getToken() { return wrapped.getToken(); }
    public boolean punch() { return wrapped.punch(); }
  }


  @Test public void obtainTicket_badArgs()
    throws Exception
  {
    final TicketManager manager = new DefaultTicketManager();
    final InetAddress   address = null; // can't be bad anyway
    final String        host = null; // can't be bad anyway

    try {
      Ticket t = manager.obtainTicket(null, address, host);
      fail("missing username not detected: "+t);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      Ticket t = manager.obtainTicket("", address, host);
      fail("empty username not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    try {
      Ticket t = manager.obtainTicket("me", address, host);
      fail("short username not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    try {
      Ticket t = manager.obtainTicket("ExcessivelyLongUsername",
                                      address, host);
      fail("long username not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    try {
      Ticket t = manager.obtainTicket("bad space", address, host);
      fail("invalid username not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    // for reference: the good case
    Ticket t = manager.obtainTicket("username", address, host);
    assertNotNull("no ticket despite good arguments", t);
  }


  @Test public void obtainTicket_duplicates()
    throws Exception
  {
    final TicketManager manager  = new DefaultTicketManager();
    final String        user1    = "testuser";
    final String        user2    = "gooduser";
    final InetAddress   address1 = InetAddress.getByName("127.0.0.1");
    final InetAddress   address2 = InetAddress.getByName("127.0.0.2");
    final String        host1    = "remotehost";
    final String        host2    = "farawayhost";

    Ticket t111 = manager.obtainTicket(user1, address1, host1);

    try {
      Ticket t = manager.obtainTicket(user1, null, null);
      fail("duplicate user not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    try {
      Ticket t = manager.obtainTicket(user2, address1, null);
      fail("duplicate address without host not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    try {
      Ticket t = manager.obtainTicket(user2, address1, host2);
      fail("duplicate address with different host not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    try {
      Ticket t = manager.obtainTicket(user2, null, host1);
      fail("duplicate host without address not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    try {
      Ticket t = manager.obtainTicket(user2, address2, host1);
      fail("duplicate host with different address not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    final String        user3    = "fineuser";

    // check good cases
    Ticket t22x = manager.obtainTicket(user2, address2, null);
    Ticket t3x2 = manager.obtainTicket(user3, null, host2);

    final String        user4    = "superbuser";
    final InetAddress   address3 = InetAddress.getByName("127.0.0.3");
    final String        host3    = "beyondhost";
    Ticket t433 = manager.obtainTicket(user4, address3, host3);
  }


  @Test public void lookupTicket_badArgs()
    throws Exception
  {
    final TicketManager manager = new DefaultTicketManager();
    final InetAddress   address = null; // can't be bad anyway
    final String        host = null; // can't be bad anyway

    Ticket good = manager.obtainTicket("gooduser", address, host);

    try {
      Ticket t = manager.lookupTicket(null, address, host);
      fail("missing token not detected: "+t);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      Ticket t = manager.lookupTicket("", address, host);
      fail("empty token not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    //@@@ test with invalid token?

    // for reference: the good case
    Ticket t = manager.lookupTicket(good.getToken(), address, host);
    assertSame("wrong lookup result", good, t);
  }


  @Test public void returnTicket_badArgs()
    throws Exception
  {
    final TicketManager manager = new DefaultTicketManager();
    final TicketManager other   = new DefaultTicketManager();
    final String       username = "testuser";
    final InetAddress   address = InetAddress.getByName("127.0.0.1");
    final String           host = "remotehost";

    Ticket good = manager.obtainTicket(username, address, host);
    Ticket bad  =   other.obtainTicket(username, address, host);

    try {
      manager.returnTicket(null);
      fail("missing ticket not detected");
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      manager.returnTicket(new TicketWrapper(good));
      fail("wrapped ticket not detected");
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      manager.returnTicket(bad);
      fail("ticket from other manager not detected");
    } catch (TicketException expected) {
      // expected
    }

    // for reference: the good case
    manager.returnTicket(good);
  }


}
