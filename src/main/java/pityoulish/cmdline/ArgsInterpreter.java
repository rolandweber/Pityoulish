/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;

import java.util.List;
import java.util.Arrays;


/**
 * Interprets command-line arguments and delegates to a {@link CommandHandler}.
 * This class assumes that the first three arguments are a server name,
 * port number, and command name. Commands may require additional arguments.
 */
public class ArgsInterpreter
{
  /** The backend handler to be invoked. */
  protected final BackendHandler backendHandler;

  /** The command handlers to be invoked. */
  protected final List<CommandHandler> cmdHandlers;


  /**
   * Creates a new arguments interpreter.
   *
   * @param backend     the backend handler to initialize
   * @param commands    the list of command handlers to delegate to
   */
  public ArgsInterpreter(BackendHandler backend,
                         List<CommandHandler> commands)
   {
     if (backend == null)
        throw new NullPointerException("BackendHandler");
     if ((commands == null) || commands.isEmpty())
        throw new NullPointerException("List of CommandHandler");

     backendHandler = backend;
     cmdHandlers = commands;
   }


  /**
   * Creates a new arguments interpreter, with varargs.
   *
   * @param backend     the backend handler to initialize
   * @param commands    the command handlers to delegate to
   */
  public ArgsInterpreter(BackendHandler backend,
                         CommandHandler... commands)
  {
    this(backend, Arrays.asList(commands));
  }


  /**
   * Parses command-line arguments and invokes a handler function.
   * If no handler function can be determined, usage information is printed.
   *
   * @param args        the command-line arguments to parse
   *
   * @return    the suggested exit code for the program, 0 for success
   *
   * @throws Exception  in case of a problem
   */
  public int handle(String[] args)
    throws Exception
  {
    final int beargc = backendHandler.getArgCount();
    if (args.length < beargc+1)
     {
       System.out.println(getUsage());
       return 1;
     }

    final String[] beargs  = Arrays.copyOfRange(args, 0, beargc);
    backendHandler.setBackend(beargs);

    final String   cmdname = args[beargc];
    final String[] cmdargs = Arrays.copyOfRange(args, 3, args.length);

    StatusCode status = new StatusCode();
    boolean handled = false;
    for(CommandHandler ch : cmdHandlers)
     {
       handled = ch.handle(status, cmdname, cmdargs);
       if (handled)
          return status.code;
     }

    System.out.println(getUsage());
    return 1;

  } // handle



  /** Generates a usage message. */
  public final String getUsage()
    throws java.io.IOException
  {
    final String eol = System.getProperty("line.separator", "\n");
    StringBuilder sb = new StringBuilder(1016);

    sb.append("command line arguments:").append(eol);
    backendHandler.describeUsage(sb, eol, "<cmd> [<arg> [...]]");
    sb.append("supported values for <cmd> are, with arguments:").append(eol);

    for(CommandHandler ch : cmdHandlers)
     {
       ch.describeUsage(sb, eol);
     }

    return sb.toString();
  }

}

