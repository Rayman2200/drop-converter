package de.drop_converter.listener;

import de.drop_converter.PluginWrapper;

public interface PluginListener
{
  public void addedPlugin(PluginWrapper plugin);

  public void removedPlugin(PluginWrapper plugin);

}
