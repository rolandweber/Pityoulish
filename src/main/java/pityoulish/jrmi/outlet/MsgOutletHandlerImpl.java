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
// PYL:keep
import pityoulish.outtake.Missing;
// PYL:cut
// PYL:end


/**
 * Default implementation of {@link MsgOutletHandler}.
 */
public class MsgOutletHandlerImpl
  implements MsgOutletHandler
{
  protected final RegistryBackendHandler regBackend;
  protected final LocalOutletFactory  outletFactory;


  /**
   * Creates a new outlet handler implementation.
   *
   * @param rbh   the backend handler
   * @param lof   the outlet factory
   */
  public MsgOutletHandlerImpl(RegistryBackendHandler rbh,
                              LocalOutletFactory     lof)
  {
    if (rbh == null)
       throw new NullPointerException("RegistryBackendHandler");
    if (lof == null)
       throw new NullPointerException("LocalOutletFactory");

    regBackend = rbh;
    outletFactory = lof;
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
    RemoteOutletManager rom = regBackend.getRemoteOutletManager();
    DirectMessageOutlet dmo = outletFactory.openLocalOutlet();

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
    } catch (Exception x) {
      // The cleanup logic in the finally{} clause should always be called,
      // but some code is missing there. The resulting MissingException
      // shadows problems with publishing and unpublishing.
      // Therefore, skip the cleanup if there was a problem.
      dmo = null;
      throw x;
      // PYL:cut
      // PYL:end
    } finally {
      // If all went well, the outlet is no longer published. But it could
      // still be called if any client has a stub. And the JVM keeps running.
      // Closing the outlet allows the JVM to terminate gracefully.
      outletFactory.closeLocalOutlet(dmo);
    }
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
       // From where can you get the user's outlet?
       // Print the outlet object... can you make sense of the output?
       // Make sure to handle errors. If the outlet is broken, the loop
       // should deliver the message to the remaining users anyway.
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

}
