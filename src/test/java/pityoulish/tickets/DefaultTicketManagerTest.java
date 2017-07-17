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

    try {
      Ticket t = manager.obtainTicket(null, address);
      fail("missing username not detected: "+t);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      Ticket t = manager.obtainTicket("", address);
      fail("empty username not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    //@@@ add more tests: too short, too long, invalid characters

    // for reference: the good case
    Ticket t = manager.obtainTicket("username", address);
    assertNotNull("no ticket despite good arguments", t);
  }


  @Test public void lookupTicket_badArgs()
    throws Exception
  {
    final TicketManager manager = new DefaultTicketManager();
    final InetAddress   address = null; // can't be bad anyway

    Ticket good = manager.obtainTicket("gooduser", address);

    try {
      Ticket t = manager.lookupTicket(null, address);
      fail("missing token not detected: "+t);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      Ticket t = manager.lookupTicket("", address);
      fail("empty token not detected: "+t);
    } catch (TicketException expected) {
      // expected
    }

    //@@@ test with invalid token?

    // for reference: the good case
    Ticket t = manager.lookupTicket(good.getToken(), address);
    assertSame("wrong lookup result", good, t);
  }


  @Test public void returnTicket_badArgs()
    throws Exception
  {
    final TicketManager manager = new DefaultTicketManager();
    final TicketManager other   = new DefaultTicketManager();
    final String       username = "testuser";
    final InetAddress   address = InetAddress.getByName("127.0.0.1");

    Ticket good = manager.obtainTicket(username, address);
    Ticket bad  =   other.obtainTicket(username, address);

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
