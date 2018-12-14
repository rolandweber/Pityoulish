/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;


/**
 * Represents a response from the Message Board Server.
 *
 * This interface allows for returning either an error message or
 * an object that represents success.
 */
public interface MsgBoardResponse<R>
{
  /**
   * Indicates whether this is a successful response.
   *
   * @return <code>true</code> for a successful response,
   *         <code>false</code> for an error response
   */
  public boolean isOK()
    ;


  /**
   * Obtains the success result.
   * Only valid when {@link #isOK} returns <code>true</code>.
   *
   * @return the result
   */
  public R getResult()
    ;


  /**
   * Obtains the error message.
   * Only valid when {@link #isOK} returns <code>false</code>.
   *
   * @return a description of the problem
   */
  public String getProblem()
    ;

}
