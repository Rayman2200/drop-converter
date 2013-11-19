/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Configuration
{

  private final JPanel panel = new JPanel();

  private final Properties props = new Properties();

  private final static String KEY_LOOK_AND_FEEL = "KEY_LOOK_AND_FEEL";

  public Configuration(File configurationFile) throws IOException
  {
    if (configurationFile.exists()) {
      if (configurationFile.isFile()) {
        try (FileInputStream fis = new FileInputStream(configurationFile)) {
          props.load(fis);
        }
      }
    } else {
      configurationFile.createNewFile();
    }

    createPanel();
  }

  public String getLookAndFeel()
  {
    return props.getProperty(KEY_LOOK_AND_FEEL, UIManager.getSystemLookAndFeelClassName());
  }

  public void setLookAndFeel(String lookAndFeel)
  {
    props.setProperty(KEY_LOOK_AND_FEEL, lookAndFeel);
  }

//  private JTextField field_lookAndFeel;
  private JComboBox<String> lookAndFeelSelector;
  
  private void createPanel()
  {
    JPanel settings = new JPanel(new GridLayout(1, 2));
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    
    
    final JButton buttonSave = new JButton("Save");
    final JButton buttonReset = new JButton("Reset");

    ActionListener l = new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (e.getSource().equals(buttonSave)) {
          // TODO: save action
        } else if (e.getSource().equals(buttonReset)) {
          // TODO: reset action
        }
      }
    };

    buttonSave.addActionListener(l);
    buttonReset.addActionListener(l);
    
    buttons.add(buttonSave);
    buttons.add(buttonReset);
    
    panel.setLayout(new BorderLayout());
    panel.add(settings,BorderLayout.CENTER);
    panel.add(buttons,BorderLayout.SOUTH);
    
    settings.add(new JLabel("Look and Feel:"));
    lookAndFeelSelector = new JComboBox<>();
    for (LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels()) 
    {
      lookAndFeelSelector.addItem(lookAndFeelInfo.getClassName());
    }
    lookAndFeelSelector.setSelectedItem(getLookAndFeel());
    settings.add(lookAndFeelSelector);
  }

  public JPanel getPanel()
  {
    return panel;
  }
}
