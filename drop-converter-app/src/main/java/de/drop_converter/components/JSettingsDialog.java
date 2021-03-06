/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.drop_converter.Converter;
import de.drop_converter.PluginHandler;
import de.drop_converter.PluginWrapper;
import de.drop_converter.listener.PluginListener;
import de.drop_converter.plugin.ConverterPlugin;

/**
 * Settings dialog that let the user configure the actual installed plugins if
 * they provide such a configuration panel.
 * 
 * @author Thomas Chojecki
 */
public class JSettingsDialog extends JDialog implements PluginListener
{
  private static final long serialVersionUID = 7990236744320792256L;

  private final JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT);

  private final Converter converter;

  private static final Logger LOGGER = Logger.getLogger(JSettingsDialog.class.getName());

  /**
   * @param parent is the parent JFrame
   * @param pluginHandler contains the plugins that can provide configuration panels.
   * @throws IllegalArgumentException if the PluginHandler is null.
   */
  public JSettingsDialog(Converter parent, PluginHandler pluginHandler) throws IllegalArgumentException
  {
    super(parent);
    this.converter = parent;
    setModal(true);
    setTitle("Settings");
    setSize(1000, 600);
    add(pane);

    pluginHandler.addPluginListener(this);

    addTab("Global settings", converter.getConfiguration().getPanel());
    addTab("Plugin configuration", new JPluginConfiguration(pluginHandler));
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
    LOGGER.fine("Plugin \"" + title + "\" provide configuration panel");
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
    if (configPanel != null)
    {
      addTab(pluginWrapper.getPluginName(), plugin.getConfigPanel());
    }
  }

  public void removeTab(PluginWrapper pluginWrapper)
  {
    int indexOfTab = pane.indexOfTab(pluginWrapper.getPluginName());
    if (indexOfTab != -1)
    {
      pane.remove(indexOfTab);
    }
  }

  @Override
  public void addedPlugin(PluginWrapper plugin)
  {
    addTab(plugin);
  }

  @Override
  public void removedPlugin(PluginWrapper plugin)
  {
    removeTab(plugin);
  };

  @Override
  public void initializedPlugin(PluginWrapper plugin)
  {}

  @Override
  public void destroyedPlugin(PluginWrapper plugin)
  {}
}
