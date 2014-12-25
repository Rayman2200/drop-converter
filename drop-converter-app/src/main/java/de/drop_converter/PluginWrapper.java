/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter;

import java.util.List;
import java.util.logging.Logger;

import de.drop_converter.listener.PluginListener;
import de.drop_converter.plugin.ConverterPlugin;
import de.drop_converter.plugin.annotations.ConverterPluginDetails;
import de.drop_converter.plugin.exception.InitializationException;

/**
 * A container and wrapper for plugins. It hold the plugin itself, the details and provide some convenience methods. It
 * is a thin layer over the drop-converter-plugin module.
 * 
 * @author Thomas Chojecki
 */
public class PluginWrapper implements Comparable<PluginWrapper>
{

  private static final Logger LOGGER = Logger.getLogger(PluginWrapper.class.getName());

  private String authorName = "";
  private String authorEmail = "";
  private String pluginName = "";
  private String pluginDescription = "";
  private String pluginVersion = "";
  private String pluginWebsite = "";

  private boolean pluginInitialized = false;
  private boolean pluginEnabled = false;

  private final ConverterPlugin plugin;

  // Hold a list with all registered listener that what to be notificated if plugin is initialized or disabled.
  private List<PluginListener> list;

  public PluginWrapper(ConverterPlugin plugin)
  {
    this.plugin = plugin;

    Class<? extends ConverterPlugin> pluginClass = plugin.getClass();

    if (pluginClass.isAnnotationPresent(ConverterPluginDetails.class))
    {
      ConverterPluginDetails annot = pluginClass.getAnnotation(ConverterPluginDetails.class);
      authorName = annot.authorName();
      authorEmail = annot.authorEmail();
      pluginName = annot.pluginName();
      pluginDescription = annot.pluginDescription();
      pluginVersion = annot.pluginVersion();
      pluginWebsite = annot.pluginWebsite();
    }
    else
    {
      pluginName = plugin.toString();
    }
  }

  public String getAuthorName()
  {
    return authorName;
  }

  public String getAuthorEmail()
  {
    return authorEmail;
  }

  public String getPluginName()
  {
    return pluginName;
  }

  public String getPluginDescription()
  {
    return pluginDescription;
  }

  public String getPluginVersion()
  {
    return pluginVersion;
  }

  public String getPluginWebsite()
  {
    return pluginWebsite;
  }

  public ConverterPlugin getPlugin()
  {
    return plugin;
  }

  /**
   * Call the initPlugin method within the Plugin. Each plugin will be initialized only once.
   * 
   * @throws InitializationException if an error occur through initialization.
   */
  public synchronized void initializePlugin() throws InitializationException
  {
    if (!isPluginInitialized())
    {
      // if the plugin was already destroyed, we can not initialize.
      if (plugin == null)
      {
        return;
      }

      plugin.initPlugin();
      LOGGER.fine("Plugin initialized: " + getPluginName());
      pluginInitialized = true;

      if (list != null)
      {
        for (PluginListener listener : list)
        {
          listener.initializedPlugin(this);
        }
      }
    }
  }

  /**
   * Call the destroyPlugin method within the Plugin. This will help terminating the plugin.
   * 
   * @throws InitializationException if an error occur through destruction
   */
  public synchronized void destroyPlugin() throws InitializationException
  {
    if (isPluginInitialized())
    {
      plugin.destroyPlugin();
      LOGGER.fine("Plugin destroyed: " + getPluginName());
      pluginInitialized = false;

      if (list != null)
      {
        for (PluginListener listener : list)
        {
          listener.destroyedPlugin(this);
        }
      }
    }
  }

  /**
   * Call the enablePlugin method within the Plugin. This will prepare the plugin for converting jobs. Will be triggered
   * for the selected plugin.
   * 
   * @throws InitializationException if an error occur through enabling.
   */
  public synchronized void enablePlugin() throws InitializationException
  {
    if (!pluginEnabled)
    {
      plugin.enablePlugin();
      LOGGER.fine("Plugin enabled: " + getPluginName());
      pluginEnabled = true;
    }
  }

  /**
   * Call the disablePlugin method within the Plugin. Will be triggered if a plugin was deselected.
   * 
   * @throws InitializationException if an error occur through enabling.
   */
  public synchronized void disablePlugin() throws InitializationException
  {
    if (pluginEnabled)
    {
      plugin.disablePlugin();
      LOGGER.fine("Plugin disabled: " + getPluginName());
      pluginEnabled = false;
    }
  }

  /**
   * Check the state of the plugin.
   * 
   * @return true if the initializePlugin method runs successful.
   */
  public boolean isPluginInitialized()
  {
    return pluginInitialized;
  }

  /**
   * Check the state of the plugin.
   * 
   * @return true if the initializePlugin method runs successful.
   */
  public boolean isPluginEnabled()
  {
    return pluginEnabled;
  }

  @Override
  public int compareTo(PluginWrapper o)
  {
    return getPluginName().compareTo(o.getPluginName());
  }

  @Override
  public String toString()
  {
    return getPluginName();
  }

  /**
   * A reference to the <code>PluginListener</code> list from the <code>PluginHandler</code>. It will be used to
   * notify all listeners if the state of this plugin changed (initialize or destroy). This state can only be obtained
   * from inside the <code>PluginWrapper</code>.
   * 
   * @param list is a reference to the listener list.
   */
  void setPluginListenerList(List<PluginListener> list)
  {
    this.list = list;
  }
}
