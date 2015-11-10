/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;


/**
 * Base class for conveniently implementing {@link CommandHandler}.
 * This class assumes that a handler supports several commands,
 * which are defined by an enum that implements {@link Command}.
 * The enum constants must be written in uppercase.
 */
public abstract class CommandHandlerBase<C extends Enum<C> & Command>
  implements CommandHandler
{
  protected final Class<C> cmdClass;

  /**
   * Creates a new command handler.
   *
   * @param cmds        the enumeration of commands to handle
   */
  protected CommandHandlerBase(Class<C> cmds)
   {
     cmdClass = cmds;
   }


  /**
   * Obtains a command by name.
   * Character case is ignored.
   *
   * @param name        the name of the command
   *
   * @return    the command, or <code>null</code> if unknown
   */
  protected C getCommand(String name)
  {
    try {
      return Enum.valueOf(cmdClass, name.toUpperCase());
    } catch (Exception ignore) { }

    return null;
  }


  /**
   * Obtains a command, if it is supported.
   *
   * @param name        the name of the command
   * @param args        the arguments provided for the command
   *
   * @return    the command, if it is known and supported with the given
   *            number of arguments; <code>null</code> otherwise
   */
  protected C checkCommand(String name, String... args)
   {
     final C cmd = getCommand(name);
     boolean ok  = ((cmd != null) &&
                    (args.length >= cmd.getMinArgs()) &&
                    (args.length <= cmd.getMaxArgs()) );
     return ok ? cmd : null;
   }

}
