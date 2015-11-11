/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tutorial;

import java.util.Collections;
//import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.InetAddress;

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
   * The tutorial commands.
   * <ul>
   * <li>latin: print a text with ISO Latin special characters</li>
   * <li>local: determine the local IP addresses</li>
   * <li>islocal: check if a hostname refers to this machine</li>
   * <li>lookup: look up the IP addresses for a hostname</li>
   * </ul>
   */
  public enum TutorialCommand implements Command
  {
    LATIN(0,0), LOCAL(0,0), ISLOCAL(1,1), LOOKUP(1,1);

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


  // non-javadoc, see interface Command
  public void describeUsage(Appendable app, String eol)
    throws java.io.IOException
  {
    //@@@ Move help text to properties files. EOL handling is annoying though.
    app.append("latin").append(eol)
      .append("  print a text with ISO Latin special characters")
      .append(eol)
      .append("local").append(eol)
      .append("  print the local IP addresses")
      .append(eol)
      .append("islocal <hostname>").append(eol)
      .append("  check whether the hostname belongs to the local host")
      .append(eol)
      .append("lookup <hostname>").append(eol)
      .append("  print the IP addresses for the hostname")
      .append(eol)
      ;
  }


  // non-javadoc, see base class
  protected int handleCommand(TutorialCommand cmd, String... args)
    throws Exception
  {
    int status = 0;
    switch(cmd)
     {
      case LATIN:   status = handleLatinCmd(); break;
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
   */
  protected int handleLatinCmd()
    throws Exception
   {
     //@@@ Move text to properties files.
     String msg =
       "H\u00e4ve you h\u00f6rd of the German umlauts, like the '\u00dc'?";
     System.out.println(msg);

     return 0;
   }


  protected int handleLocalCmd()
    throws Exception
   {
     for(NetworkInterface nwi :
           Collections.list(NetworkInterface.getNetworkInterfaces()))
      {
        //@@@ Move text to properties files. Yes, the simple ones too.
        System.out.println("Network Interface '"+nwi.getName()+"'");

        for(InetAddress ina : Collections.list(nwi.getInetAddresses()))
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

           System.out.println(sb);
         }
      }

     return 0;
   }


  protected int handleIsLocalCmd(String hostname)
    throws Exception
   {
     throw new UnsupportedOperationException("@@@ not yet implemented");
   }


  protected int handleLookupCmd(String hostname)
    throws Exception
   {
     throw new UnsupportedOperationException("@@@ not yet implemented");
   }

}
