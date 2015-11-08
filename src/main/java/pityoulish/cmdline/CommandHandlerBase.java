/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.cmdline;


/**
 * Base class for conveniently implementing {@link CommandHandler}.
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


  // non-javadoc, see interface
  public boolean supports(String name, String... args)
   {
     final C cmd = getCommand(name);

     return ((cmd != null) &&
             (args.length >= cmd.getMinArgs()) &&
             (args.length <= cmd.getMaxArgs()) );
   }

}
