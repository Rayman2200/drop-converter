/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.plugin.exception;

/**
 * This exception can be thrown while initializing or destroying a plugin. Also for enabling or disabling it. In such a
 * case, the plugin will be gayed out and not used.
 * 
 * @author Thomas Chojecki
 */
public class InitializationException extends ConverterException
{
  private static final long serialVersionUID = 4293795181187512999L;

  public InitializationException()
  {
    super();
  }

  public InitializationException(String string)
  {
    super(string);
  }

  public InitializationException(Throwable t)
  {
    super(t);
  }

  public InitializationException(String string, Throwable t)
  {
    super(string, t);
  }
}
