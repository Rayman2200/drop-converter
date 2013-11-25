/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.drop_converter.listener.PluginListener;
import de.drop_converter.plugin.ConverterPlugin;
import de.drop_converter.plugin.exception.InitializationException;

/**
 * Handle the drop-converter-plugins. New plugins can be
 * 
 * @author Thomas Chojecki
 */
public class PluginHandler
{

  private static final Logger LOG = Logger.getLogger(PluginHandler.class.getName());

  private final Set<PluginWrapper> plugins = new TreeSet<>();

  private List<String> pluginExclusions = new ArrayList<>();

  private final List<PluginListener> listenerList = new ArrayList<>();

  private boolean closed = false;

  /**
   * Load all plugins that can be find via the <code>ClassLoader</code>.
   * 
   * @param cl is the <code>ClassLoader</code> where the plugins should be searched.
   * @param pluginExclusions is a list with plugins that need to be excluded.
   */
  public void loadPlugins(ClassLoader cl)
  {
    for (ConverterPlugin converterPlugin : ServiceLoader.load(ConverterPlugin.class, cl)) {
      registerPlugin(converterPlugin);
    }
  }

  /**
   * Register new plugins.
   * 
   * @param plugin is the loaded plugin that should be add to the ComboBox
   */
  public void registerPlugin(ConverterPlugin plugin)
  {
    PluginWrapper pluginWrapper = new PluginWrapper(plugin);
    pluginWrapper.setPluginListenerList(Collections.unmodifiableList(listenerList));

    if (pluginExclusions.contains(plugin.getClass().getName())) {
      LOG.info("Plugin disabled by the user: " + pluginWrapper.getPluginName());
    } else {
      try {
        // Initialize the plugin
        pluginWrapper.initializePlugin();
      } catch (InitializationException e) {
        LOG.log(Level.SEVERE, "Plugin initialization failed: " + pluginWrapper.getPluginName()
            + " could not be initialized.", e);
      }
    }

    plugins.add(pluginWrapper);

    // fire listener
    for (PluginListener listener : listenerList) {
      listener.addedPlugin(pluginWrapper);
    }
  }

  /**
   * Return all registered plugins.
   * 
   * @return a Set of plugins wrapped inside the <code>ConverterPluginsWrapper</code>
   */
  public Set<PluginWrapper> getPlugins()
  {
    return Collections.unmodifiableSet(plugins);
  }

  /**
   * Register PluginLister that notify if a plugin was added or removed.
   * 
   * @param listener is the listener interface.
   */
  public void addPluginListener(PluginListener listener)
  {
    if (!listenerList.contains(listener)) {
      listenerList.add(listener);
    }
  }

  /**
   * Dispose the PluginHandler and disable/destroy the plugins.
   */
  public synchronized void dispose()
  {
    if (!closed) {
      LOG.info("Shutting down the plugin handler and all registered plugins.");
      Iterator<PluginWrapper> iterator = plugins.iterator();
      while (iterator.hasNext()) {
        PluginWrapper pluginWrapper = iterator.next();

        try {
          if (pluginWrapper.isPluginEnabled()) {
            pluginWrapper.disablePlugin();
          }

          if (pluginWrapper.isPluginInitialized()) {
            pluginWrapper.destroyPlugin();
          }
        } catch (InitializationException e) {
          // nothing to do
        }
      }
      closed = true;
    } else {
      LOG.info("PluginHandler already shut down. Skipping.");
    }
  }

  /**
   * Set a list of plugin class names that should not be initialized. (blacklisted plugins)
   */
  public void setPluginExclusion(List<String> disabledPlugins)
  {
    pluginExclusions = disabledPlugins;

  }

}
