/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.drop_converter.Converter;
import de.drop_converter.PluginHandler;
import de.drop_converter.components.JSettingsDialog;

/**
 * 
 * @author Thomas Chojecki
 *
 */
public class SettingsContext extends AbstractAction
{

  private final JSettingsDialog jSettingsDialog;
  
  public SettingsContext(Converter parent, PluginHandler pluginHandler)
  {
    super("Settings");
    jSettingsDialog = new JSettingsDialog(parent, pluginHandler);
    jSettingsDialog.setModal(true);
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    jSettingsDialog.setVisible(true);
  }

}
