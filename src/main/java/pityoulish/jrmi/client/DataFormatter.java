/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.client;

import pityoulish.jrmi.api.MessageList;


/**
 * Interface for pretty-printing data.
 */
public interface DataFormatter
{
  public void printMessageList(MessageList msglist)
    ;

  public void printTicket(String ticket)
    ;

}
