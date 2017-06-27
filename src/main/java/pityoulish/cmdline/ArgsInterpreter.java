/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;


/**
 * Interprets command-line arguments and delegates to a {@link CommandHandler}.
 * This class assumes that a fixed number of arguments describe the backend.
 * Then follows a command name, and a variable number of command-specific
 * arguments. Programs that support only one command can provide its name
 * to the constructor, instead of expecting it on the command line.
 */
public class ArgsInterpreter
{
  /** The backend handler to be invoked. */
  protected final BackendHandler backendHandler;

  /** The command handlers to be invoked. */
  protected final List<CommandHandler> cmdHandlers;

  /** The implied command name, if only one command is supported. */
  protected final String onlyCommand;


  /**
   * Creates a new arguments interpreter.
   *
   * @param backend     the backend handler to initialize
   * @param onlycmd     the name of the only command, or <code>null</code>.
   *        If a value is passed here, the command-line arguments cannot
   *        include a command name. If <code>null</code> is passed here,
   *        a command name on the command line is mandatory.
   * @param commands    the list of command handlers to delegate to
   */
  public ArgsInterpreter(BackendHandler backend,
                         String onlycmd,
                         List<CommandHandler> commands)
  {
    if (backend == null)
       throw new NullPointerException("BackendHandler");
    if ((commands == null) || commands.isEmpty())
       throw new NullPointerException("List of CommandHandler");

    backendHandler = backend;
    onlyCommand = onlycmd;
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
    this(backend, null, Arrays.asList(commands));
  }


  /**
   * Creates a new arguments interpreter, with implied command.
   * There's only a single handler, nothing else makes sense.
   *
   * @param backend     the backend handler to initialize
   * @param onlycmd     the name of the only command
   * @param cmdhandler  the command handler to delegate to
   */
  public ArgsInterpreter(BackendHandler backend,
                         String onlycmd,
                         CommandHandler cmdhandler)
  {
    this(backend, onlycmd, Collections.singletonList(cmdhandler));
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

    int minargc = beargc;
    if (onlyCommand == null)
       minargc++;

    if (args.length < minargc)
     {
       System.out.println(getUsage());
       return 1;
     }

    final String[] beargs  = Arrays.copyOfRange(args, 0, beargc);
    backendHandler.setBackend(beargs);

    final String   cmdname =
      (onlyCommand != null) ? onlyCommand : args[beargc];
    final String[] cmdargs = Arrays.copyOfRange(args, minargc, args.length);

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
    StringBuilder sb = new StringBuilder(1016);

    sb.append(Catalog.USAGE_INTRO.lookup()).append('\n');
    backendHandler.describeUsage(sb, Catalog.CMD_AND_ARGS.lookup());
    sb.append('\n')
      .append(Catalog.CMD_HEADING.lookup()).append('\n');

    for(CommandHandler ch : cmdHandlers)
     {
       ch.describeUsage(sb);
     }

    return Catalog.fixEOL(sb.toString());
  }

}

