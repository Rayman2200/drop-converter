/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.drop_converter.PluginHandler;
import de.drop_converter.PluginWrapper;
import de.drop_converter.plugin.ConverterPlugin;

/**
 * Settings dialog that let the user configure the actual installed plugins if they provide such a configuration panel.
 * 
 * @author Thomas Chojecki
 */
public class JSettingsDialog extends JDialog
{

  private final JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT);

  private static final Logger LOG = Logger.getLogger(JSettingsDialog.class.getName());

  private PluginHandler pluginHandler;

  /**
   * @param parent is the parent JFrame
   * @param pluginHandler contains the plugins that can provide configuration panels.
   * @throws IllegalArgumentException if the PluginHandler is null.
   */
  public JSettingsDialog(JFrame parent, PluginHandler pluginHandler) throws IllegalArgumentException
  {
    super(parent);
    setModal(true);
    setTitle("Settings");
    setSize(800, 600);
    add(pane);
  }

  @Override
  public void setVisible(boolean b)
  {
    // recreating the JTabbedPane.

    pane.removeAll();
    addTab("Global settings", createConverterConfiguration());
    addTab("Plugin configuration", new JPluginConfiguration(pluginHandler));

    super.setVisible(b);
  }

  private JPanel createConverterConfiguration()
  {
    JPanel panel = new JPanel();
    panel.add(new JLabel("Hallo Welt"));
    return panel;
  }

  public void setPluginHandler(PluginHandler pluginHandler)
  {
    this.pluginHandler = pluginHandler;
  }

  public void reloadPlugins()
  {

  }

  /**
   * Add new Tab to the settings dialog.
   * 
   * @param title is the tab title
   * @param comp is the plugin configuration panel
   */
  public void addTab(String title, Component comp)
  {
    pane.addTab(title, comp);
    LOG.fine("Plugin \"" + title + "\" provide configuration panel");
  }

  /**
   * Add plugin configuration panel to a JTabbedPane.
   * 
   * @param pluginWrapper is the plugin which configuration panel should be add to the JTabbedPane.
   */
  public void addTab(PluginWrapper pluginWrapper)
  {
    ConverterPlugin plugin = pluginWrapper.getPlugin();
    JPanel configPanel = plugin.getConfigPanel();
    if (configPanel != null) {
      addTab(pluginWrapper.getPluginName(), plugin.getConfigPanel());
    }
  }

  public void removeTab(PluginWrapper pluginWrapper)
  {
    // pane.g

  }
}
