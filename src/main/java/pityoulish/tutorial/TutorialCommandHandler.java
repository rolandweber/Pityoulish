/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tutorial;

import java.util.Collections;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;

import pityoulish.outtake.Missing;
import pityoulish.cmdline.Command;
import pityoulish.cmdline.CommandHandlerBase;


/**
 * A handler for the commands used in the Tutorial.
 */
//@@@ Separate dispatching and handling into distinct classes? For example,
//@@@ abstract TutorialCommandHandler and concrete TutorialCommandHandlerImpl?
//@@@ Not needed here, but the actual exercises might use this pattern.
public class TutorialCommandHandler
  extends CommandHandlerBase<TutorialCommandHandler.TutorialCommand>
{
  /**
   * List of the tutorial commands.
   * <ul>
   * <li>latin: print a text with ISO Latin special characters</li>
   * <li>local: determine the local IP addresses</li>
   * <li>islocal: check if a hostname refers to this machine</li>
   * <li>lookup: look up the IP addresses for a hostname</li>
   * </ul>
   */
  public enum TutorialCommand implements Command
  {
    LATIN(0,80), LOCAL(0,0), ISLOCAL(1,1), LOOKUP(1,1);

    public final int minArgs;
    public final int maxArgs;

    private TutorialCommand(int mina, int maxa)
     {
       minArgs = mina;
       maxArgs = maxa;
     }

    public final int getMinArgs() { return minArgs; }
    public final int getMaxArgs() { return maxArgs; }
  }


  public TutorialCommandHandler()
  {
    super(TutorialCommand.class);
  }


  // non-javadoc, see interface CommandHandler
  public void describeUsage(Appendable app)
    throws IOException
  {
    app.append(Catalog.USAGE.lookup());
  }


  // non-javadoc, see base class
  protected int handleCommand(TutorialCommand cmd, String... args)
    throws Exception
  {
    int status = 0;
    switch(cmd)
     {
      case LATIN:   status = handleLatinCmd(args); break;
      case LOCAL:   status = handleLocalCmd(); break;
      case ISLOCAL: status = handleIsLocalCmd(args[0]); break;
      case LOOKUP:  status = handleLookupCmd(args[0]); break;
      default:
        throw new UnsupportedOperationException(String.valueOf(cmd));
     }

    return status;
  }


  /**
   * Prints a text that contains special characters from an ISO Latin charset.
   * Use this to verify that your environment settings are correct.
   * If necessary, specify the default character set as a Java system property
   * when starting the program.
   *
   * @param args   additional strings to echo.
   *               Use this to test the input encoding.
   */
  protected int handleLatinCmd(String... args)
    throws Exception
   {
     System.out.println(Catalog.LATIN_SAMPLE.lookup());

     for (String arg : args)
        System.out.println(arg);

     return 0;
   }


  /**
   * Prints the local IP addresses for all network interfaces.
   */
  protected int handleLocalCmd()
    throws Exception
   {
     for (NetworkInterface nwi :
            Collections.list(NetworkInterface.getNetworkInterfaces()))
      {
        System.out.println(Catalog.NW_IFCE_1.format(nwi.getName()));

        for (InetAddress ina : Collections.list(nwi.getInetAddresses()))
         {
           System.out.println(formatInetAddress(ina));
         }
      }

     return 0;
   }


  /**
   * Checks whether a hostname refers to the local host.
   *
   * @param hostname    the hostname or string-encoded IP address to check
   *
   * @return    0 if the argument is the local host, -1 if it isn't
   *
   * @throws Exception  in case of a problem
   */
  protected int handleIsLocalCmd(String hostname)
   {
     // this may perform a DNS lookup, with all sorts of resulting problems
     InetSocketAddress isa = new InetSocketAddress(hostname, 0);

     int result = -1;
     try {
       // if the address is local, we can bind a socket to it
       // PYL:keep
       Missing.here("bind a socket to a hostname");
       Missing.pretend(IOException.class);
       // PYL:cut
       Socket so = new Socket();
       so.bind(isa);
       // PYL:end

       result = 0;
       System.out.println(Catalog.HOST_IS_LOCAL_1.format(hostname));
       // The ephemereal port chosen for binding is now allocated and
       // cannot be used elsewhere for some time, typically minutes.

     } catch (IOException iox) {
       System.out.println(iox.toString());
       result = -1;
       System.out.println(Catalog.HOST_NOT_LOCAL_1.format(hostname));
     }

     return result;
   }


  /**
   * Performs a DNS lookup on a hostname and prints the results.
   *
   * @param hostname    the hostname or string-encoded IP address
   *                    for which to perform a DNS lookup
   */
  protected int handleLookupCmd(String hostname)
   {
     System.out.println(Catalog.LOOKING_UP_1.format(hostname));

     // PYL:keep
     Missing.here("look up the InetAddress(es) of a hostname");
     // Use formatInetAddress below to print all the addresses.
     // Also handle the case when the hostname has no address at all.
     // PYL:cut
     try {
       for (InetAddress ina : InetAddress.getAllByName(hostname))
        {
          System.out.println(formatInetAddress(ina));
        }
     } catch (java.net.UnknownHostException uhx) {
       System.out.println(uhx.getMessage()); // no need for the stack trace
     }
     // PYL:end

     return 0;
   }


  /**
   * Formats an {@link InetAddress} into a readable string.
   */
  public static String formatInetAddress(InetAddress ina)
   {
     String ipaddr = ina.getHostAddress();
     String hostname = ina.getHostName();
     String fqhostname = ina.getCanonicalHostName();

     if (fqhostname.equals(hostname))
        fqhostname = null;
     if (hostname.equals(ipaddr))
        hostname = null;

     StringBuilder sb = new StringBuilder(80);
     sb.append("   ").append(ipaddr);
     if (hostname != null)
        sb.append("   ").append(hostname);
     if (fqhostname != null)
        sb.append("   ").append(fqhostname);

     return sb.toString();
   }
}
