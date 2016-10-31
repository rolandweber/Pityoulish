/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

import java.util.List;
import java.util.Arrays;

import pityoulish.jrmi.api.RemoteOutletManager;
import pityoulish.jrmi.api.DirectMessageOutlet;


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
  public void openOutlet(String ticket, int limit)
    throws Exception
  {
    checkForHostnameProperty();

    throw new UnsupportedOperationException("@@@ not yet implemented");
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
       try {
         DirectMessageOutlet dmo = rom.getOutlet(username);
         System.out.println(dmo); // NLS?
         dmo.deliverMessage(originator, text);
       } catch (Exception x) {
         System.out.println(x.toString()); // no trace
       }
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
