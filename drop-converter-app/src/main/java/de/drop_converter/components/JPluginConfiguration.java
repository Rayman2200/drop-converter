/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.drop_converter.PluginHandler;
import de.drop_converter.PluginWrapper;

/**
 * Configure the installed plugins.
 * 
 * @author Thomas Chojecki
 */
public class JPluginConfiguration extends JPanel
{
  private final PluginHandler pluginHandler;

  private JList<PluginWrapper> pluginList;

  private JTable pluginOverviewe = new JTable();

  public JPluginConfiguration(PluginHandler pluginHandler)
  {
    this.pluginHandler = pluginHandler;

    add(new JLabel("Dummy plugin configuration."));

    // Set<PluginWrapper> plugins = pluginHandler.getPlugins();

    add(new JScrollPane(pluginOverviewe));

  }
}
