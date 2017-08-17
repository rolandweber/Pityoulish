/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.jrmi.server;

import pityoulish.i18n.TextEntry;
import pityoulish.mbserver.ProblemFactory;
import pityoulish.mbserver.StringProblemFactory;
import pityoulish.jrmi.api.APIException;


/**
 * A {@link ProblemFactory} that creates API exceptions.
 */
public class APIProblemFactory implements ProblemFactory<APIException>
{
  // non-javadoc, see interface
  public APIException newProblem(TextEntry te, Object... params)
  {
    // The exception is returned, not thrown.
    // Logging will be done by the thrower.
    return new APIException(StringProblemFactory.te2string(te, params));
  }

}
