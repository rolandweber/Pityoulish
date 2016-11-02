/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

// PYL:keep
// PYL:cut
import java.rmi.server.UnicastRemoteObject;
// PYL:end
import java.util.List;
import java.util.Arrays;

import pityoulish.jrmi.api.RemoteOutletManager;
import pityoulish.jrmi.api.DirectMessageOutlet;
import pityoulish.outtake.Missing;


/**
 * Default implementation of {@link MsgOutletHandler}.
 */
public class MsgOutletHandlerImpl
  implements MsgOutletHandler
{
  protected final RegistryBackendHandler regBackend;


  /**
   * Creates a new outlet handler implementation.
   *
   * @param rbh   the backend handler
   */
  public MsgOutletHandlerImpl(RegistryBackendHandler rbh)
  {
    if (rbh == null)
       throw new NullPointerException("RegistryBackendHandler");

    regBackend = rbh;
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void listUsernames()
    throws Exception
  {
    RemoteOutletManager rom = regBackend.getRemoteOutletManager();

    List<String> usernames = rom.listUsernames();
    if (usernames.size() < 1)
     {
       System.out.println(Catalog.REPORT_NO_OUTLET_0.format());
     }

    for (String username : usernames)
       System.out.println(username);
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void openOutlet(String ticket, int seconds)
    throws Exception
  {
    checkForHostnameProperty();

    RemoteOutletManager rom = regBackend.getRemoteOutletManager();
    DirectMessageOutlet dmo = createLocalOutlet();

    // PYL:keep
    boolean noproblem = false;
    // PYL:cut
    // PYL:end
    try {
      System.out.println(Catalog.REPORT_OPEN_PUBLISH_0.format());
      // It doesn't matter whether the object or its stub is published.
      // The RMI logic will pass the stub to the registry either way.
      rom.publishOutlet(ticket, dmo);

      System.out.println(Catalog.REPORT_OPEN_WAITING_1.format(seconds));
      Thread.sleep(seconds*1000);

      System.out.println(Catalog.REPORT_OPEN_UNPUBLISH_0.format());
      rom.unpublishOutlet(ticket);

      // PYL:keep
      noproblem = true;
      // PYL:cut
      // PYL:end
    } finally {
      // PYL:keep
      // The "Missing" exception could shadow other problems, for example
      // an invalid ticket. Throw it only if there is no other problem.
      // Please remove the 'noproblem' flag from the code when you fix it.
      if (noproblem)
         Missing.here("close the outlet for remote method invocations");
      // At this point, the outlet should become unavailable. However,
      // the RMI runtime is still ready to serve remote calls. You'll
      // notice that the JVM keeps running if you don't clean up here.
      // Calling System.exit would be cheating. Tell RMI to stop serving
      // remote calls for the outlet, then the JVM terminates gracefully.
      // PYL:cut
      UnicastRemoteObject.unexportObject(dmo, true);
      // PYL:end
    }
  }


  /**
   * Creates an outlet and makes it available for remote method invocations.
   * Called from {@link #openOutlet}.
   *
   * @return    the outlet or its stub, it doesn't matter which
   */
  protected DirectMessageOutlet createLocalOutlet()
    throws Exception
  {
    DirectMessageOutletImpl dmoi = null;
    DirectMessageOutlet     stub = null;

    // PYL:keep
    Missing.here("create a local outlet");
    // The JavaDocs and signature of this method, along with
    // the variables declared here, should be clear enough.
    // An import from java.rmi.server is missing though.
    // PYL:cut
    dmoi = new DirectMessageOutletImpl();
    stub = (DirectMessageOutlet) UnicastRemoteObject.exportObject(dmoi, 0);
    // PYL:end

    System.out.println(Catalog.REPORT_OPEN_OUTLET_1.format(dmoi));
    System.out.println(Catalog.REPORT_OPEN_STUB_1.format(stub));

    return dmoi;
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void sendMessage(String originator, String text, String... recipients)
    throws Exception
  {
    RemoteOutletManager rom = regBackend.getRemoteOutletManager();

    List<String> usernames = null;

    if ((recipients.length == 1) && "*".equals(recipients[0]))
     {
       System.out.println(Catalog.REPORT_LIST_OUTLETS_0.format());
       usernames = rom.listUsernames();
       if (usernames.size() < 1)
        {
          System.out.println(Catalog.REPORT_NO_OUTLET_0.format());
        }
     }
    else
     {
       usernames = Arrays.asList(recipients);
     }

    for (String username: usernames)
     {
       System.out.println(Catalog.REPORT_SENDING_TO_1.format(username));
       // PYL:keep
       Missing.here("deliver the message to the outlet of '"+username+"'");
       // ...and don't forget to handle errors. If the outlet is broken,
       // the loop should deliver the message to the remaining users anyway.
       // PYL:cut
       try {
         DirectMessageOutlet dmo = rom.getOutlet(username);
         System.out.println(dmo); // NLS?
         dmo.deliverMessage(originator, text);
       } catch (Exception x) {
         System.out.println(x.toString()); // no trace
       }
       // PYL:end
     }
  }


  // non-javadoc, see interface MsgBoardClientHandler
  public void unpublishOutlet(String ticket)
    throws Exception
  {
    RemoteOutletManager rom = regBackend.getRemoteOutletManager();

    rom.unpublishOutlet(ticket);
  }


  /**
   * Checks for the system property that specifies the hostname for RMI.
   * If that property is not set, stubs created by this JVM point to
   * some version of localhost and are useless on other machines.
   * This method prints a message to System.out, indicating whether the
   * property is set or not.
   *
   * @return    <code>true</code> if the property is set and not empty,
   *            <code>false</code> otherwise
   */
  public final static boolean checkForHostnameProperty()
  {
    final String name = "java.rmi.server.hostname";
    final String value = System.getProperty(name, null);

    final boolean ok = (value != null) && (value.length() > 0);
    if (ok)
     {
       System.out.println(Catalog.REPORT_JRMI_HOSTNAME_1.format
                          (value));
     }
    else
     {
       // Not a fatal problem, still good enough for local development.
       System.out.println(Catalog.REPORT_JRMI_HOSTNAME_PROPERTY_1.format
                          (name));
     }

    return ok;
  }

}
