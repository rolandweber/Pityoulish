/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.sockets.server;


/**
 * Default implementation of {@link MsgBoardRequest}.
 * All attributes are read-only.
 */
public class MsgBoardRequestImpl implements MsgBoardRequest
{
  public final ReqType mbrType;

  public final Integer mbrLimit;

  public final String mbrMarker;

  public final String mbrTicket;

  public final String mbrText;

  public final String mbrOriginator;


  /**
   * Creates a new request object.
   */
  public MsgBoardRequestImpl(ReqType rt,
                             Integer lim,
                             String mark,
                             String tick,
                             String txt,
                             String orig)
  {
    if (rt == null)
       throw new NullPointerException("ReqType");
    // all other arguments are optional

    mbrType = rt;
    mbrLimit = lim;
    mbrMarker = mark;
    mbrTicket = tick;
    mbrText = txt;
    mbrOriginator = orig;
  }


  public static MsgBoardRequest newListMessages(Integer lim, String mark)
  {
    return new MsgBoardRequestImpl(ReqType.LIST_MESSAGES,
                                   lim, mark, null, null, null);
  }

  public static MsgBoardRequest newPutMessage(String tick, String txt)
  {
    return new MsgBoardRequestImpl(ReqType.PUT_MESSAGE,
                                   null, null, tick, txt, null);
  }

  public static MsgBoardRequest newObtainTicket(String orig)
  {
    return new MsgBoardRequestImpl(ReqType.OBTAIN_TICKET,
                                   null, null, null, null, orig);
  }

  public static MsgBoardRequest newReturnTicket(String tick)
  {
    return new MsgBoardRequestImpl(ReqType.RETURN_TICKET,
                                   null, null, tick, null, null);
  }

  public static MsgBoardRequest newReplaceTicket(String tick)
  {
    return new MsgBoardRequestImpl(ReqType.REPLACE_TICKET,
                                   null, null, tick, null, null);
  }



  public final ReqType getReqType()
  {
    return mbrType;
  }

  public final Integer getLimit()
  {
    return mbrLimit;
  }

  public final String getMarker()
  {
    return mbrMarker;
  }

  public final String getTicket()
  {
    return mbrTicket;
  }

  public final String getText()
  {
    return mbrText;
  }

  public final String getOriginator()
  {
    return mbrOriginator;
  }


  public String toString()
  {
    StringBuilder sb = new StringBuilder(200);

    sb.append("MsgBoardRequest").append(mbrType);

    if (mbrLimit != null)
       sb.append('<').append(mbrLimit);
    if (mbrMarker != null)
       sb.append('#').append(mbrMarker);
    if (mbrTicket != null)
       sb.append("+'").append(mbrTicket).append("'");
    if (mbrText != null)
       sb.append(":\"").append(mbrText).append("\"");
    if (mbrOriginator != null)
       sb.append('@').append(mbrOriginator);

    return sb.toString();
  }


}
