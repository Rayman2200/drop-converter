/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.drop_converter.PluginHandler;
import de.drop_converter.PluginWrapper;
import de.drop_converter.listener.PluginListener;

/**
 * Configure the installed plugins.
 * 
 * @author Thomas Chojecki
 */
public class JPluginConfiguration extends JPanel implements PluginListener
{
  private final PluginHandler pluginHandler;

  private final DefaultTableModel model;

  private final JTable pluginOverviewe;

  public JPluginConfiguration(PluginHandler pluginHandler)
  {
    this.pluginHandler = pluginHandler;
    pluginHandler.addPluginListener(this);
    setLayout(new BorderLayout());
    add(new JLabel("Dummy plugin configuration."), BorderLayout.NORTH);

    Set<PluginWrapper> plugins = pluginHandler.getPlugins();
    Iterator<PluginWrapper> it = plugins.iterator();

    String[] t = new String[] { "Name", "Description", "Version", "Website", "Author", "Email", "Enabled?" };
    model = new DefaultTableModel(t, 0);
    
    while (it.hasNext()) {
      addPlugin((PluginWrapper) it.next());
    }

    pluginOverviewe = new JTable(model);
    add(new JScrollPane(pluginOverviewe), BorderLayout.CENTER);

  }

  private void addPlugin(PluginWrapper p)
  {
    Object[] v = new Object[] { p.getPluginName(), p.getPluginDescription(), p.getPluginVersion(), 
                                p.getPluginWebsite(), p.getAuthorName(),p.getAuthorEmail(), new JCheckBox("", true) };
    model.addRow(v);
  }

  @Override
  public void addedPlugin(PluginWrapper plugin)
  {
    addPlugin(plugin);
  }

  @Override
  public void removedPlugin(PluginWrapper plugin)
  {

  }
}
