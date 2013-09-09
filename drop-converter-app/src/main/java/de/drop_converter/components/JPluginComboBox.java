/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.TransferHandler;

import de.drop_converter.ConverterPluginWrapper;
import de.drop_converter.plugin.exception.InitializationException;

/**
 * A plugin chooser
 * 
 * @author Thomas Chojecki
 */
public class JPluginComboBox extends JComboBox<ConverterPluginWrapper>
{
  private static final long serialVersionUID = -2505280449969300955L;
  private final static Logger LOG = Logger.getLogger(JPluginComboBox.class.getName());

  private ConverterPluginWrapper lastPlugin;

  /**
   * Refresh the view.
   * 
   * @param pluginHandler
   */
  public void reloadPlugins(Collection<ConverterPluginWrapper> plugins)
  {
    Model model = new Model(plugins);
    setModel(model);

    addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          pluginStateChanged((ConverterPluginWrapper) e.getItem());
        }
      }
    });

    if (plugins.size() != 0) {
      setSelectedIndex(0);
    }
  }

  /**
   * If the plugin state changed, we need to disable the old plugin and enable the new one.
   * 
   * @param oldPlugin the old plugin that was deselected.
   * @param newPlugin the new plugin that was selected.
   */
  private void pluginStateChanged(ConverterPluginWrapper newPlugin)
  {
    if (lastPlugin != null && lastPlugin.isPluginEnabled()) {
      try {
        lastPlugin.disablePlugin();
      } catch (InitializationException ex) {
        LOG.log(Level.SEVERE, "Could not disable deselected plugin " + lastPlugin.getPluginName(), ex);
      }
    }
    lastPlugin = newPlugin;
    try {
      lastPlugin.enablePlugin();
    } catch (InitializationException ex) {
      LOG.log(Level.SEVERE, "Could not enable selected plugin " + lastPlugin.getPluginName(), ex);
    }
  }

  /**
   * Handle incoming drag and drops for the ComboBox. New plugins can be drag and droped into the ComboBox to install it
   * into the plugin directory.
   * 
   * @author Thomas Chojecki
   */
  private class JPluginComboBoxTransferHandler extends TransferHandler
  {
    @Override
    public boolean canImport(TransferSupport support)
    {
      // TODO Need to be implemented
      return super.canImport(support);
    }

    @Override
    public boolean importData(TransferSupport support)
    {
      // TODO Need to be implemented
      return super.importData(support);
    }
  }

  /**
   * A simple model to display the plugins.
   * 
   * @author Thomas Chojecki
   */
  private static class Model extends AbstractListModel<ConverterPluginWrapper> implements
      ComboBoxModel<ConverterPluginWrapper>
  {
    private static final long serialVersionUID = -8935531015863045662L;

    private List<ConverterPluginWrapper> plugins;

    private Object selectedItem;

    public Model(Collection<ConverterPluginWrapper> plugins)
    {
      if (plugins instanceof List) {
        this.plugins = (List<ConverterPluginWrapper>) plugins;
      } else {
        this.plugins = new ArrayList<ConverterPluginWrapper>(plugins);
      }
    }

    @Override
    public int getSize()
    {
      return plugins.size();
    }

    @Override
    public ConverterPluginWrapper getElementAt(int index)
    {
      return plugins.get(index);
    }

    @Override
    public void setSelectedItem(Object anItem)
    {
      if ((selectedItem != null && !selectedItem.equals(anItem)) || selectedItem == null && anItem != null) {
        selectedItem = anItem;
        fireContentsChanged(this, -1, -1);
      }
    }

    @Override
    public Object getSelectedItem()
    {
      return selectedItem;
    }
  }
}
