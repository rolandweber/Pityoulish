/*
 * This work is released into the Public Domain under the
 * terms of the Creative Commons CC0 1.0 Universal license.
 * https://creativecommons.org/publicdomain/zero/1.0/
 */
package pityoulish.i18n;


/**
 * Reference to a resource bundle.
 */
public interface BundleRef
{
  /**
   * Obtains the name of the resource bundle.
   *
   * @return    the name of the bundle in which to look up texts,
   *            not <code>null</code>
   */
  public String getBundleName()
    ;


  /**
   * Obtains the class loader of the resource bundle.
   *
   * @return    the class loader from which to load the bundle,
   *            not <code>null</code>
   */
  public ClassLoader getClassLoader()
    ;

}

