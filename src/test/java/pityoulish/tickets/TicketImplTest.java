/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

import java.net.InetAddress;

import org.junit.*;
import static org.junit.Assert.*;


public class TicketImplTest
{
  //@@@ use JMockit to stub the TicketManager instances

  /**
   * Creates a ticket implementation.
   * Mandatory <code>null</code> arguments will be set to defaults.
   */
  protected static TicketImpl newTicketImpl(TicketManager creator,
                                            String       username,
                                            InetAddress   address,
                                            String          token,
                                            long           expiry,
                                            int           actions)
  {
    if (creator == null)
       creator = new DefaultTicketManager();
    if (username == null)
       username = "j1user";
    // InetAddress is optional
    if (token == null)
       token = "token/"+username;
    // expiry is primitive
    // actions is primitive

    return new TicketImpl(creator, username, address,
                          token, expiry, actions);
  }
                                            


  @Test public void newObject_fullArgs()
    throws Exception
  {
    final TicketManager creator = new DefaultTicketManager();
    final String       username = "j1user";
    final InetAddress   address = InetAddress.getByName("127.0.0.1");
    final String          token = "token/"+username;
    final long           expiry = 1234567890L;
    final int           actions = 8;

    TicketImpl ti = new TicketImpl(creator, username, address,
                                   token, expiry, actions);
    // if there's no exception, we're good

    assertNotNull("no object", ti);
    assertEquals("wrong creator", creator, ti.issuedBy);
    assertEquals("wrong username", username, ti.getUsername());
    assertEquals("wrong address", address, ti.getAddress());
    assertEquals("wrong token", token, ti.getToken());
    assertEquals("wrong expiry", expiry, ti.expiryTime);
    assertEquals("wrong #actions", actions, ti.actionsRemaining);
  }


  @Test public void newObject_minArgs()
    throws Exception
  {
    final TicketManager creator = new DefaultTicketManager();
    final String       username = "j1user";
    final InetAddress   address = null;
    final String          token = "token/"+username;
    final long           expiry = 1234567890L;
    final int           actions = 0;

    Ticket t = new TicketImpl(creator, username, address,
                              token, expiry, actions);
    // if there's no exception, we're good

    assertNotNull("no object", t);
  }


  @Test public void newObject_badArgs()
    throws Exception
  {
    final TicketManager creator = new DefaultTicketManager();
    final String       username = "j1user";
    final InetAddress   address = InetAddress.getByName("127.0.0.1");
    final String          token = "token/"+username;
    final long           expiry = 1234567890L;
    final int           actions = 8;

    try {
      Ticket t = new TicketImpl(null, username, address,
                                token, expiry, actions);
      fail("missing TicketManager not detected, ticket="+t);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      Ticket t = new TicketImpl(creator, null, address,
                                token, expiry, actions);
      fail("missing username not detected, ticket="+t);
    } catch (RuntimeException expected) {
      // expected
    }

    try {
      Ticket t = new TicketImpl(creator, username, address,
                                null, expiry, actions);
      fail("missing token not detected, ticket="+t);
    } catch (RuntimeException expected) {
      // expected
    }

  }


  @Test public void isExpired_true()
  //throws Exception
  {
    final long   expiry = 1234567890L;
    final TicketImpl ti = newTicketImpl(null, null, null, null, expiry, 8);

    boolean expired = ti.isExpired(expiry);

    assertEquals("should be expired", true, expired);
  }


  @Test public void isExpired_false()
  //throws Exception
  {
    final long   expiry = 1234567890L;
    final TicketImpl ti = newTicketImpl(null, null, null, null, expiry, 8);

    boolean expired = ti.isExpired(expiry-1L);

    assertEquals("should not be expired", false, expired);
  }


  @Test public void isExpired_now()
  //throws Exception
  {
    final long   expiry = System.currentTimeMillis();
    final TicketImpl ti = newTicketImpl(null, null, null, null, expiry, 8);

    boolean expired = ti.isExpired();

    assertEquals("should be expired", true, expired);
  }


  @Test public void punch()
    throws Exception
  {
    final TicketImpl ti = newTicketImpl(null, null, null, null, 0L, 2);

    boolean action1 = ti.punch();
    boolean action2 = ti.punch();
    boolean action3 = ti.punch();
    boolean action4 = ti.punch();

    assertTrue ("action 1 should pass", action1);
    assertTrue ("action 2 should pass", action2);
    assertFalse("action 3 should stop", action3);
    assertFalse("action 4 should stop", action4);
  }


  @Test public void validate_OK()
    throws Exception
  {
    final TicketManager creator = new DefaultTicketManager();
    final String       username = "j1user";
    final InetAddress   address = InetAddress.getByName("127.0.0.1");
    final String          token = "token/"+username;
    final long           expiry = System.currentTimeMillis()+8000L;
    final int           actions = 8;

    // There's a race condition here: if running this test takes
    // longer than 8 secs, the ticket expires and validation fails.

    TicketImpl ti = new TicketImpl(creator, username, address,
                                   token, expiry, actions);
    try {
      ti.validate(creator, username, address, token);
    } catch (TicketException tx) {
      tx.printStackTrace();
      fail("full data should validate");
    }

    try {
      ti.validate(creator, null, address, token);
    } catch (TicketException tx) {
      tx.printStackTrace();
      fail("absent username should validate");
    }

    try {
      ti.validate(creator, username, null, token);
    } catch (TicketException tx) {
      tx.printStackTrace();
      fail("absent address should validate");
    }
  }


  @Test public void validate_KO()
    throws Exception
  {
    final TicketManager creator = new DefaultTicketManager();
    final String       username = "j1user";
    final InetAddress   address = InetAddress.getByName("127.0.0.1");
    final String          token = "token/"+username;
    final long           expiry = System.currentTimeMillis()+8000L;
    final int           actions = 8;

    // There's a race condition here: if this test runs longer than 8 secs,
    // the ticket expires and validation fails for the wrong reason.

    TicketImpl ti = new TicketImpl(creator, username, address,
                                   token, expiry, actions);

    try {
      final TicketManager creator2 = new DefaultTicketManager();
      ti.validate(creator2, username, address, token);
      fail("wrong creator should not validate");
    } catch (TicketException expected) {
      // expected
    }

    try {
      final String username2 = "j2user";
      ti.validate(creator, username2, address, token);
      fail("wrong username should not validate");
    } catch (TicketException expected) {
      // expected
    }

    try {
      final InetAddress address2 = InetAddress.getByName("127.0.0.2");
      ti.validate(creator, username, address2, token);
      fail("wrong address should not validate");
    } catch (TicketException expected) {
      // expected
    }

    try {
      final String token2 = "token2/"+username;
      ti.validate(creator, username, address, token2);
      fail("wrong token should not validate");
    } catch (TicketException expected) {
      // expected
    }

    // assert that the ticket has not yet expired
    try {
      ti.validate(creator, username, address, token);
    } catch (TicketException expected) {
      fail("ticket expired while testing");
    }
  }


}
