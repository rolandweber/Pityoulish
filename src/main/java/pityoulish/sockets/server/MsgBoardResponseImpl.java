/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;

import pityoulish.msgboard.MessageBatch;


/**
 * Default implementation of {@link MsgBoardResponse}.
 * Represents a response from the Message Board Server.
 * This class cannot be instantiated, use the nested classes instead.
 */
public class MsgBoardResponseImpl<R> implements MsgBoardResponse<R>
{
  protected final R result;

  protected final String problem;


  /**
   * Creates a success response.
   *
   * @param what   the result to wrap
   */
  private MsgBoardResponseImpl(R what)
  {
    if (what == null)
       throw new NullPointerException("what");

    result = what;
    problem = null;
  }


  /**
   * Creates an error response.
   *
   * @param clazz   the class that would have been wrapped for success
   * @param why     a description of the problem
   */
  private MsgBoardResponseImpl(Class<? extends R> clazz, String why)
  {
    if (why == null)
       throw new NullPointerException("why");
    // clazz is not used, but needed for type inference

    result = null;
    problem = why;
  }


  public static class Error extends MsgBoardResponseImpl<String>
  {
    public Error(String problem)
    {
      super(String.class, problem);
    }
  }


  public static class Info extends MsgBoardResponseImpl<String>
  {
    public Info(String text)
    {
      super(text);
    }
  }


  public static class Ticket extends MsgBoardResponseImpl<String>
  {
    public Ticket(String tictok)
    {
      super(tictok);
    }
  }


  public static class Batch extends MsgBoardResponseImpl<MessageBatch>
  {
    public Batch(MessageBatch mb)
    {
      super(mb);
    }
  }


  public static class BatchError extends MsgBoardResponseImpl<MessageBatch>
  {
    public BatchError(String problem)
    {
      super(MessageBatch.class, problem);
    }
  }



  // non-javadoc, see interface
  public final boolean isOK()
  {
    return (problem == null);
  }


  // non-javadoc, see interface
  public final R getResult()
  {
    return result;
  }


  // non-javadoc, see interface
  public final String getProblem()
  {
    return problem;
  }


  /**
   * Provide a human-readable description of this response.
   *
   * @return a description
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder(80);

    // strip "pityoulish.sockets.server." from classname
    sb.append(this.getClass().getName().substring(26));

    if (result != null)
       sb.append(":+:").append(result);
    else
       sb.append(":-:").append(problem);

    return sb.toString();
  }

}
