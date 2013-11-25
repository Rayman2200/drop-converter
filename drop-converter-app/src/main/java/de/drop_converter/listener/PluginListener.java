/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.listener;

import de.drop_converter.PluginWrapper;

public interface PluginListener
{
  public void addedPlugin(PluginWrapper plugin);

  public void removedPlugin(PluginWrapper plugin);

  public void initializedPlugin(PluginWrapper plugin);

  public void destroyedPlugin(PluginWrapper plugin);
}
