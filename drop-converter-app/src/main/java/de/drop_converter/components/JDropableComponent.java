/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import de.drop_converter.PluginWrapper;
import de.drop_converter.plugin.exception.ConverterException;

/**
 * A drag and drop area that will be filled with an image. The component has the same size as the given image.
 * 
 * @author Thomas Chojecki
 */
public class JDropableComponent extends JComponent
{

  private static final long serialVersionUID = -3072978732941239567L;
  private final static Logger LOG = Logger.getLogger(JDropableTransferHandler.class.getName());

  private final JPluginComboBox pluginChooser;
  private final Image img;

  /**
   * Create a new drag and drop area that will be filled with the given image. The component has the same size as the
   * given image.
   * 
   * @param img is the Image that should be shown.
   * @param pluginChooser is the ComboBox which organize the plugins.
   */
  public JDropableComponent(Image img, JPluginComboBox pluginChooser)
  {
    this.pluginChooser = pluginChooser;
    setTransferHandler(new JDropableTransferHandler());
    this.img = img;
  }

  @Override
  public void paint(Graphics g)
  {
    g.drawImage(img, 0, 0, getWidth(), getHeight(), new Color(255, 220, 200), this);
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(img.getWidth(this), img.getHeight(this));
  }
  
  /**
   * Set the context menu
   */
  public void setContextMenu(ContextMenu menu) 
  {
    addMouseListener(menu);
  }

  /**
   * Handle the drag and drop for this component.
   * 
   * @author Thomas Chojecki
   */
  private class JDropableTransferHandler extends TransferHandler
  {
    private static final long serialVersionUID = -6594115979366685023L;

    @Override
    public boolean canImport(TransferSupport support)
    {
      PluginWrapper selectedItem = (PluginWrapper) pluginChooser.getSelectedItem();
      if (selectedItem != null) {
        support.setDropAction(COPY);
        return selectedItem.getPlugin().canImport(support);
      }
      return false;
    }

    @Override
    public boolean importData(TransferSupport support)
    {
      PluginWrapper selectedItem = (PluginWrapper) pluginChooser.getSelectedItem();
      if (selectedItem != null) {
        try {
          return selectedItem.getPlugin().importData(support);
        } catch (ConverterException e) {
          String error = "Performing convert with plugin " + selectedItem.getPluginName() + " failed.";
          String message = "The plugin throws an error durring the convert: " + e.getMessage();

          LOG.log(Level.SEVERE, message, e);
          JOptionPane.showMessageDialog(pluginChooser, message, error, JOptionPane.ERROR_MESSAGE);
        }
      }
      return false;
    }
  }
}
