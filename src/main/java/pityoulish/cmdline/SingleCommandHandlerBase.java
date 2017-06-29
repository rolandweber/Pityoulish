/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;


/**
 * Base class for handling a single, implicit command.
 */
public abstract class SingleCommandHandlerBase
  implements CommandHandler
{
  /** A dummy command name, automatically generated. */
  protected final String commandName;

  protected final int minArgs;
  protected final int maxArgs;


  /**
   * Creates a new handler for a single command.
   *
   * @param mina   the minimum number of arguments
   * @param maxa   the maximum number of arguments
   */
  protected SingleCommandHandlerBase(int mina, int maxa)
  {
    if (mina < 0)
       throw new IllegalArgumentException("minArgs "+mina+" < 0");
    if (maxa < mina)
       throw new IllegalArgumentException("maxArgs "+maxa+" < "+mina);

    commandName = super.toString();

    minArgs = mina;
    maxArgs = maxa;
  }


  /**
   * Obtains the command name expected by this handler.
   * This should be passed to the {@link ArgsInterpreter} constructor.
   *
   * @return the name of the only command supported by this handler
   */
  public final String getCommandName()
  {
    return commandName;
  }


  // non-javadoc, see interface CommandHandler
  public boolean handle(StatusCode status, String name, String... args)
    throws Exception
   {
     if (!commandName.equals(name) ||
         (args.length < minArgs)   || 
         (args.length > maxArgs)
         )
        return false;

     status.code = handleCommand(args);
     return true;
   }


  /**
   * Called by {@link #handle} for the only supported command.
   *
   * @param args    the arguments
   *
   * @return    the status code
   *
   * @throws Exception  in case of a problem
   */
  protected abstract int handleCommand(String... args)
    throws Exception
    ;

}
