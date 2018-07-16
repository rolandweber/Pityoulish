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

import pityoulish.jrmi.api.DirectMessageOutlet;
// PYL:keep
import pityoulish.outtake.Missing;
// PYL:cut
// PYL:end


/**
 * Default implementation of {@link LocalOutletFactory}.
 */
public class LocalOutletFactoryImpl implements LocalOutletFactory
{
  // non-javadoc, see interface LocalOutletFactory
  public DirectMessageOutlet openLocalOutlet()
    throws Exception
  {
    checkForHostnameProperty();

    DirectMessageOutletImpl dmoi = null;
    DirectMessageOutlet     stub = null;

    // PYL:keep
    Missing.here("create a local outlet and export it");
    // Exporting is implemented by subclasses of java.rmi.server.RemoteServer.
    // You'll need a typecast:  stub = (DirectMessageOutlet) ...whatever...
    // To get around a StubNotFoundException, use a different export method.
    // PYL:cut
    dmoi = new DirectMessageOutletImpl();
    stub = (DirectMessageOutlet) UnicastRemoteObject.exportObject(dmoi, 0);
    // PYL:end

    System.out.println(Catalog.REPORT_OPEN_OUTLET_1.format(dmoi));
    System.out.println(Catalog.REPORT_OPEN_STUB_1.format(stub));

    return dmoi;
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


  // non-javadoc, see interface LocalOutletFactory
  public void closeLocalOutlet(DirectMessageOutlet outlet)
    throws Exception
  {
    if (outlet == null)
       return;

    // PYL:keep
    Missing.here("close the outlet for remote method invocations");
    // As long as an outlet is exported, the RMI runtime keeps the JVM
    // running to serve possible calls. Unexport the outlet to let the
    // JVM terminate gracefully. Calling System.exit would be cheating.
    // PYL:cut
    UnicastRemoteObject.unexportObject(outlet, true);
    // PYL:end
  }

}
