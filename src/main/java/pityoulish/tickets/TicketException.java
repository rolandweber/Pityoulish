/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.tickets;

//@@@ JavaDocs need cleanup. Find a better place for these explanations.

/**
 * Indicates a ticketing problem.
 * There are different ways to deal with this exception in the remote API:
 * <ol>
 * <li>Exception wrapping. When a {@link TicketException} is thrown,
 *     it must be caught and should be replaced for example by a
 *     <code>APIException</code> that is available on the caller side,
 *     too. This cleanly separates the ticketing module from the remote API.
 *     The only tie between the two is in the module that uses ticketing
 *     to implement the remote API.
 *     <br/>
 *     This is the cleanest solution from a design perspective, compared to
 *     the options below.
 *     But it requires ugly exception handling code in the implementation.
 *     Note that wrapping and re-throwing the exception is not enough.
 *     The {@link TicketException} must never be thrown through the remote API,
 *     not even wrapped. See the following option for an explanation.
 *     </li>
 * <li>Derive {@link TicketException} from an exception declared in the
 *     remote API, for example the <code>APIException</code>.
 *     This ties the ticketing module to the (utility classes for) the
 *     remote API and makes it harder to use in a different context.
 *     At least this option would drag in additional classes that are
 *     not really meant for a different context.
 *     <br/>
 *     While this would be enough to let the solution compile and work in
 *     the good cases, it would also introduce an undesirable trait.
 *     When the {@link TicketException} is throw on the server side and
 *     passed on to the remote caller, Java RMI will attempt to deserialize
 *     the exception on the client side. If the exception class is not
 *     installed there, this causes a <code>java.rmi.UnmarshalException</code>
 *     to be thrown on the client. The original exception message will not
 *     be seen on the client side.
 *     </li>
 * <li>Declare the {@link TicketException} in the remote interface.
 *     This exposes an implementation detail in the API, namely
 *     that the ticketing module is used on the server side.
 *     It explicitly requires the exception class to be present on the client
 *     side, although the ticketing module is not meant for client-side use.
 *     <br/>
 *     Compared to the previous option, this makes the dependency on the
 *     implementation class explicitly visible. But it will not even allow
 *     the good cases to execute without the {@link TicketException}
 *     on the client side.
 *     </li>
 * </ol>
 */
public class TicketException extends Exception
{
  public TicketException(String msg)
  {
    super(msg);
  }

  public TicketException(String msg, Throwable cause)
  {
    super(msg, cause);
  }
}

