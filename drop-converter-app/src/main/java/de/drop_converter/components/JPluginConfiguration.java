/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import de.drop_converter.PluginHandler;
import de.drop_converter.PluginWrapper;
import de.drop_converter.listener.PluginListener;
import de.drop_converter.plugin.exception.InitializationException;

/**
 * Configure the installed plugins.
 * 
 * @author Thomas Chojecki
 */
public class JPluginConfiguration extends JPanel implements PluginListener
{
  private final JTable pluginOverview;

  private final List<PluginWrapper> plugins;

  public JPluginConfiguration(PluginHandler pluginHandler)
  {
    pluginHandler.addPluginListener(this);
    plugins = new ArrayList<>(pluginHandler.getPlugins());

    setLayout(new BorderLayout());
    add(new JLabel("Dummy plugin configuration."), BorderLayout.NORTH);

    pluginOverview = new JTable(new TableModel());
    add(new JScrollPane(pluginOverview), BorderLayout.CENTER);
  }

  @Override
  public void addedPlugin(PluginWrapper plugin)
  {
    plugins.add(plugin);
  }

  @Override
  public void removedPlugin(PluginWrapper plugin)
  {
    plugins.remove(plugin);
  }

  @Override
  public void initializedPlugin(PluginWrapper plugin)
  {}

  @Override
  public void destroyedPlugin(PluginWrapper plugin)
  {}

  class TableModel extends AbstractTableModel
  {

    private final String[] columnNames = new String[] { "Name", "Description", "Version", "Website", "Author", "Email", "Enabled?" };

    @Override
    public int getRowCount()
    {
      return plugins.size();
    }

    @Override
    public int getColumnCount()
    {
      return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
      return columnIndex == 6 ? true : false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
      boolean flag = (boolean) aValue;
      PluginWrapper pluginWrapper = plugins.get(rowIndex);
      try
      {
        if (flag)
        {
          pluginWrapper.initializePlugin();
        }
        else
        {
          if (pluginWrapper.isPluginEnabled())
          {
            pluginWrapper.disablePlugin();
          }
          pluginWrapper.destroyPlugin();
        }
      }
      catch (InitializationException e)
      {

      }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
      PluginWrapper pluginWrapper = plugins.get(rowIndex);

      switch (columnIndex)
      {
        case 0:
          return pluginWrapper.getPluginName();
        case 1:
          return pluginWrapper.getPluginDescription();
        case 2:
          return pluginWrapper.getPluginVersion();
        case 3:
          return pluginWrapper.getPluginWebsite();
        case 4:
          return pluginWrapper.getAuthorName();
        case 5:
          return pluginWrapper.getAuthorEmail();
        case 6:
          return pluginWrapper.isPluginInitialized();
      }
      return null;
    }

    @Override
    public String getColumnName(int column)
    {
      return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
      return columnIndex == 6 ? Boolean.class : String.class;
    }
  }
}
