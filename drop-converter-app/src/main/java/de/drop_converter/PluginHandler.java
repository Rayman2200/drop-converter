/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter;

import java.util.ArrayList;
import java.util.Collections;
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

  private final List<PluginListener> listenerList = new ArrayList<>();

  /**
   * Load all plugins that can be find via the <code>ClassLoader</code>.
   * 
   * @param cl is the <code>ClassLoader</code> where the plugins should be searched.
   * @param pluginExclusions is a list with plugins that need to be excluded.
   */
  public void loadPlugins(ClassLoader cl, List<String> pluginExclusions)
  {
    // TODO: The plugin exclusion need to be implemented.
    if (pluginExclusions == null) {
      pluginExclusions = Collections.emptyList();
    }

    for (ConverterPlugin converterPlugin : ServiceLoader.load(ConverterPlugin.class, cl)) {
      PluginWrapper container = new PluginWrapper(converterPlugin);

      registerPlugin(container);
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

    if (pluginExclusions.contains(converterPlugin.getClass().getName())) {
      LOG.info("Plugin disabled by the user: " + container.getPluginName());
    } else {
      try {
        // Initialize the plugin
        container.initializePlugin();
      } catch (InitializationException e) {
        LOG.log(Level.SEVERE, "Plugin initialization failed: " + container.getPluginName()
            + " could not be initialized.", e);
      }
    }

    plugins.add(pluginWrapper);

    // fire listener
    for (PluginListener listener : listenerList) {
      listener.addedPlugin(plugin);
    }
  }

  private PluginWrapper wrapConverterPlugin(ConverterPlugin plugin)
  {

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

  public void addPluginListener(PluginListener listener)
  {
    if (!listenerList.contains(listener)) {
      listenerList.add(listener);
    }
  }
}
