/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Prototype of a context menu.
 * 
 * @author Thomas Chojecki
 */
public class ContextMenu implements MouseListener, ActionListener
{

  private final JPopupMenu menu = new JPopupMenu();

  private final JMenuItem configure = new JMenuItem("Configure Plugins");

  private final JMenuItem exit = new JMenuItem("Exit");

  public ContextMenu()
  {
    configure.addActionListener(this);
    exit.addActionListener(this);

    // TODO replace the JMenuItems by Actions
    menu.add(configure);
    menu.add(exit);
  }

  public void showMenu(MouseEvent e)
  {
    menu.show(e.getComponent(), e.getX(), e.getY());
  }

  @Override
  public void mouseClicked(MouseEvent e)
  {}

  @Override
  public void mouseEntered(MouseEvent e)
  {}

  @Override
  public void mouseExited(MouseEvent e)
  {}

  @Override
  public void mousePressed(MouseEvent e)
  {
    if (e.isPopupTrigger()) {
      showMenu(e);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (e.isPopupTrigger()) {
      showMenu(e);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    Object source = e.getSource();
    if (source.equals(exit)) {
      System.exit(0);
    } else if (source.equals(configure)) {
      System.out.println("Configuration");
    }

  }
}
