/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JPopupMenu;

/**
 * Prototype of a context menu.
 * 
 * @author Thomas Chojecki
 */
public class ContextMenu extends MouseAdapter
{

  private final JPopupMenu menu = new JPopupMenu();

  public void addMenuEntry(Action menuEntry)
  {
    menu.add(menuEntry);
  }

  public void addMenuEntry(Component menuEntry)
  {
    menu.add(menuEntry);
  }

  public void showMenu(MouseEvent e)
  {
    menu.show(e.getComponent(), e.getX(), e.getY());
  }

  @Override
  public void mousePressed(MouseEvent e)
  {
    if (e.isPopupTrigger())
    {
      showMenu(e);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (e.isPopupTrigger())
    {
      showMenu(e);
    }
  }
}
