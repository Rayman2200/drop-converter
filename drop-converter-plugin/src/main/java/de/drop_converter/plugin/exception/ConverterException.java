/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.plugin.exception;

/**
 * Specific plugin exception that can be used for wrapping all kind of exceptions. A plugin creator can extend this
 * Exception for his own purpose. This exception will be logged in the DropConverter app.
 * 
 * @author Thomas Chojecki
 */
public class ConverterException extends Exception
{
  private static final long serialVersionUID = 440842411642243852L;

  public ConverterException()
  {
    super();
  }

  public ConverterException(String string)
  {
    super(string);
  }

  public ConverterException(Throwable t)
  {
    super(t);
  }

  public ConverterException(String string, Throwable t)
  {
    super(string, t);
  }

}
