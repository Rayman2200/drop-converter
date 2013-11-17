/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ShortFormatter extends Formatter
{
  //
  // Create a DateFormat to format the logger timestamp.
  //
  private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

  @Override
  public String format(LogRecord record)
  {
    StringBuilder builder = new StringBuilder(1000);
    builder.append(df.format(new Date(record.getMillis()))).append(" - ");
    builder.append('[').append(record.getSourceClassName()).append('.');
    builder.append(record.getSourceMethodName()).append("] - ");
    builder.append('[').append(record.getLevel()).append("] - ");
    builder.append(formatMessage(record));
    builder.append('\n');
    return builder.toString();
  }

  @Override
  public String getHead(Handler h)
  {
    return super.getHead(h);
  }

  @Override
  public String getTail(Handler h)
  {
    return super.getTail(h);
  }

  public static void main(String[] args)
  {
    Logger logger = Logger.getLogger(ShortFormatter.class.getName());
    logger.setUseParentHandlers(false);

    ShortFormatter formatter = new ShortFormatter();
    ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter(formatter);

    logger.addHandler(handler);
    logger.info("Example of creating custom formatter.");
    logger.warning("A warning message.");
    logger.severe("A severe message.");
  }
}
