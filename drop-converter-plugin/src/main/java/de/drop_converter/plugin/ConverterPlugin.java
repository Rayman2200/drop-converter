/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.plugin;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JPanel;
import javax.swing.TransferHandler.TransferSupport;

import de.drop_converter.plugin.exception.ConverterException;
import de.drop_converter.plugin.exception.InitializationException;

/**
 * Base interface for new Plugins.
 * 
 * @author Thomas Chojecki
 */
public interface ConverterPlugin
{

  /**
   * Describe the version of the plugin definition. This help the converter handling out dated plugins.
   */
  public final static String PLUGIN_VERSION = "1.0.0.0";

  /**
   * Will be called one time during plugin initialization. This function is equal to a constructor. Use it for basic
   * initalization of the plugin.
   */
  public void initPlugin() throws InitializationException;

  /**
   * Will be called if the DropConverter shut down. This function is like a destructor.
   */
  public void destroyPlugin() throws InitializationException;

  /**
   * After a user select a Plugin from the list, this method will be called. This method will be triggered each time a
   * user select this plugin. It should be used for initialize memory intensive data.
   */
  public void enablePlugin() throws InitializationException;

  /**
   * After a user deselect this Plugin from the list, this method will be called. This method will be triggered each
   * time a user deselect this plugin. It should be used for clean up the initialized memory intensive data.
   */
  public void disablePlugin() throws InitializationException;

  /**
   * <p>
   * Will be triggered frequential if the user hover a content over the dragable area of the application. If the plugin
   * can handle the given data, it should return true which will invoke the {@link #importData(TransferSupport)} method.
   * This method will also be called for content from the system clipboard.
   * </p>
   * <p>
   * Keep in mind, that this method will be called more than one time in a second and should not contain computationally
   * intensive operations.
   * </p>
   * 
   * @see {@link javax.swing.TransferHandler#canImport(TransferSupport)}
   * @param support is the TransferSupport object, that contains the <code>{@link DataFlavor}</code>s for the
   *          <code>{@link Transferable}</code>
   * @return true if this plugin can handle the <code>{@link DataFlavor}</code> by this <code>TransferSupport</code>.
   */
  public boolean canImport(TransferSupport support);

  /**
   * @param support is the TransferSupport object, that contains the <code>{@link DataFlavor}</code>s for the
   *          <code>{@link Transferable}</code>
   * @return true if this plugin finished handling the <code>Transferable</code>.
   * @throws ConverterException if an error occurred while processing the <code>Transferable</code>. This will trigger a
   *           dialog, so the user can see what goes wrong.
   */
  public boolean importData(TransferSupport support) throws ConverterException;

  /**
   * Creates a panel that offer the user a way to configure the plugin. This panel will be available through a
   * ContextMenu.
   * 
   * @return
   */
  public JPanel getConfigPanel();

}
