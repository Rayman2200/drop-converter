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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Configuration store.
 * 
 * @author Thomas Chojecki
 */
public class Configuration
{

  private final JPanel panel = new JPanel();

  private final Properties props = new Properties();

  private final static String KEY_LOOK_AND_FEEL = "look_and_feel";
  private final static String KEY_DISABLED_PLUGINS = "disabled_plugins";
  private final static String KEY_WINDOW_POSITION = "window_position";

  private final File configurationFile;

  public Configuration(File configurationFile) throws IOException
  {
    this.configurationFile = configurationFile;
    if (configurationFile.exists())
    {
      if (configurationFile.isFile())
      {
        try (FileInputStream fis = new FileInputStream(configurationFile))
        {
          props.load(fis);
        }
      }
    }
    else
    {
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

  // private JTextField field_lookAndFeel;
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
        if (e.getSource().equals(buttonSave))
        {
          // TODO: save action
        }
        else if (e.getSource().equals(buttonReset))
        {
          // TODO: reset action
        }
      }
    };

    buttonSave.addActionListener(l);
    buttonReset.addActionListener(l);

    buttons.add(buttonSave);
    buttons.add(buttonReset);

    panel.setLayout(new BorderLayout());
    panel.add(settings, BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);

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

  /**
   * Return a list of class names of disabled plugins.
   * 
   * @return list with class names as string.
   */
  public List<String> getDisabledPlugins()
  {
    String property = props.getProperty(KEY_DISABLED_PLUGINS);
    if (property == null || property.trim().isEmpty())
    {
      return Collections.EMPTY_LIST;
    }
    else
    {
      return Arrays.asList(property.split(","));
    }
  }

  /**
   * detect all not initialized plugins and save it in the configuration delimited by a comma.
   * 
   * @param plugins is a collection of plugins that should be scanned for not initialized plugins.
   */
  public void setDisabledPlugins(Collection<PluginWrapper> plugins)
  {
    if (!plugins.isEmpty())
    {
      StringBuilder sb = new StringBuilder();
      Iterator<PluginWrapper> it = plugins.iterator();
      if (!it.hasNext())
      {
        return;
      }

      while (true)
      {
        PluginWrapper p = it.next();
        if (!p.isPluginInitialized())
        {
          sb.append(p.getPlugin().getClass().getName());
          sb.append(',');
        }
        if (!it.hasNext())
        {
          props.setProperty(KEY_DISABLED_PLUGINS, sb.toString());
          return;
        }
      }
    }
  }

  /**
   * Store the actual configuration to a file.
   * 
   * @throws IOException if writing this property list to the output throws an IOException.
   */
  public void storeConfiguration() throws IOException
  {
    try (OutputStream out = new FileOutputStream(configurationFile))
    {
      props.store(out, null);
    }
  }
}
