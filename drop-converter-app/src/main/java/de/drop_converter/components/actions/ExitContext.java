/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.components.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.drop_converter.Converter;

/**
 * 
 * @author Thomas Chojecki
 * 
 */
public class ExitContext extends AbstractAction
{
  
  private Converter converter;

  public ExitContext(Converter converter)
  {
    super("Exit");
    this.converter = converter;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    converter.dispose();
  }

}
