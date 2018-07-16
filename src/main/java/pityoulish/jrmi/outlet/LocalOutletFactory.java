/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.outlet;

import pityoulish.jrmi.api.DirectMessageOutlet;


/**
 * A factory to open and close message outlets.
 */
public interface LocalOutletFactory
{
  /**
   * Creates an outlet and makes it available for remote method invocations.
   *
   * @return    the outlet or its stub, it doesn't matter which
   */
  public DirectMessageOutlet openLocalOutlet()
    throws Exception
    ;


  /**
   * Makes an outlet unavailable for remote method invocations.
   *
   * @param outlet   the outlet to dispose of
   */
  public void closeLocalOutlet(DirectMessageOutlet outlet)
    throws Exception
    ;

}
