package de.drop_converter.components;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import de.drop_converter.PluginWrapper;
import de.drop_converter.PluginHandler;

public class JPluginConfiguration extends JPanel
{
  private final PluginHandler pluginHandler;

  private JList<PluginWrapper> pluginList;

  public JPluginConfiguration(PluginHandler pluginHandler)
  {
    this.pluginHandler = pluginHandler;

    add(new JLabel("Dummy plugin configuration."));
  }
}
