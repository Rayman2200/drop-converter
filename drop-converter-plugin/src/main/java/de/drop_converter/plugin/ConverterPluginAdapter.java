/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.plugin;

import javax.swing.JPanel;

/**
 * A abstract adapter class for plugin implementation. Implements all optional functions.
 * 
 * @author Thomas Chojecki
 */
public abstract class ConverterPluginAdapter implements ConverterPlugin
{
  @Override
  public void initPlugin()
  {}

  @Override
  public void destroyPlugin()
  {}

  @Override
  public void enablePlugin()
  {}

  @Override
  public void disablePlugin()
  {}

  @Override
  public JPanel getConfigPanel()
  {
    return null;
  }
}
