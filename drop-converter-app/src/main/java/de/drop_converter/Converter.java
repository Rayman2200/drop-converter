/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import de.drop_converter.components.JDropableComponent;
import de.drop_converter.components.JPluginComboBox;

/**
 * Main executable class. Provide the GUI and initialization.
 * 
 * @author Thomas Chojecki
 */
public class Converter extends JFrame
{

  private static final long serialVersionUID = 907314946871184488L;

  // Default init
  private final Position position = Position.LOWER_RIGHT;

  private final JLabel labelTop = new JLabel(" ");

  private final JLabel labelBottom = new JLabel(" ");

  private final JPluginComboBox pluginsChooser;

  private final PluginHandler pluginHandler;

  public final static File CONVERTER_CONFIG_DIR = new File(System.getProperty("user.home"), ".drop_converter");

  public final static File CONVERTER_PLUGIN_DIR = new File(CONVERTER_CONFIG_DIR, "plugins");

  public final static File CONVERTER_LOGGING_DIR = new File(CONVERTER_CONFIG_DIR, "logging");

  enum Position
  {
    UPPER_LEFT, UPPER, UPPER_RIGHT, RIGHT, LOWER_RIGHT, LOWER, LOWER_LEFT, LEFT
  }

  static {
    // Init the logging system from the logging.properties provided inside the jar or through IDE.
    InputStream inputStream = null;
    try {
      inputStream = Converter.class.getResourceAsStream("/logging.properties");
      LogManager.getLogManager().readConfiguration(inputStream);
    } catch (IOException e) {
      System.err.println("Could not init logging");
    }
  }

  private final Logger LOGGER = Logger.getLogger(Converter.class.getName());

  public Converter()
  {
    pluginHandler = new PluginHandler();
    pluginsChooser = new JPluginComboBox(pluginHandler);

    // init gui
    init();

    // init the directories, if they currently not exist.
    if (!CONVERTER_PLUGIN_DIR.exists()) {
      if (!CONVERTER_LOGGING_DIR.mkdirs()) {
        System.err.println("Could not create logging directory");
        System.exit(1);
      }
      if (!CONVERTER_PLUGIN_DIR.mkdirs()) {
        System.err.println("Could not create plugin directory");
        System.exit(1);
      }
      // After all directories was created we can skip searching plugins.
      return;
    }

    FilenameFilter jarFiles = new FilenameFilter()
    {

      @Override
      public boolean accept(File dir, String name)
      {
        return name.endsWith(".jar");
      }
    };

    // Search for plugins in the plugin directory and load it.
    // TODO: Read also directories with complex plugin structures.
    // TODO: Read also zip files for complex plugins.
    // TODO: Seperate ClassLoader for each plugin.
    ArrayList<URL> urls = new ArrayList<URL>();
    for (File pluginFile : CONVERTER_PLUGIN_DIR.listFiles(jarFiles)) {
      try {
        urls.add(pluginFile.toURI().toURL());
      } catch (MalformedURLException e) {
        LOGGER.warning("Could not load Plugin: " + pluginFile.getAbsolutePath());
      }
    }

    URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]));
    pluginHandler.loadPlugins(urlClassLoader, null);
    pluginsChooser.reloadPlugins();
  }

  /**
   * Do some graphical initialization
   */
  private void init()
  {
    setAlwaysOnTop(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    setTitle("Converter");
    labelBottom.setBackground(Color.WHITE);
    labelTop.setBackground(Color.WHITE);

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e1) {
      LOGGER.log(Level.FINE, "Could not load system look and feel", e1);
    }

    try {
      Dimension screenSize = getToolkit().getScreenSize();
      Image image = null;

      URL resource = getClass().getResource("/images/dragdrop-150.png");
      if (resource == null) {
        LOGGER.warning("Could not find drop-image.");
        image = createFallbackImage();
      } else {
        image = ImageIO.read(resource);
      }

      add(pluginsChooser, BorderLayout.NORTH);
      add(new JDropableComponent(image, pluginsChooser), BorderLayout.CENTER);
      pack();
      // alignment need to be done after pack
      alignWindow(getSize(), screenSize, position);
    } catch (HeadlessException e) {
      LOGGER.severe("converter can not be run in a headless envirment.");
      System.exit(1);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexcpected exception. ", e);
      System.exit(1);
    }
  }

  /**
   * Align the converter window to a specific position on the screen.
   * 
   * @param frameSize is the size of the converter window
   * @param screenSize is the size of the screen
   * @param position is the Position where the windows should be shown.
   */
  private void alignWindow(Dimension frameSize, Dimension screenSize, Position position)
  {
    int width = screenSize.width;
    int height = screenSize.height;

    int x = 0;
    int y = 0;

    switch (position)
    {
      case UPPER_LEFT:
        break;
      case UPPER:
        x = width / 2 - frameSize.width / 2;
        break;
      case UPPER_RIGHT:
        x = width - frameSize.width;
        break;
      case RIGHT:
        x = width - frameSize.width;
        y = height / 2 - frameSize.height / 2;
        break;
      case LOWER_RIGHT:
        x = width - frameSize.width;
        y = height - frameSize.height;
        break;
      case LOWER:
        x = width / 2 - frameSize.width / 2;
        y = height - frameSize.height;
        break;
      case LOWER_LEFT:
        y = height - frameSize.height;
        break;
      case LEFT:
        y = height / 2 - frameSize.height / 2;
        break;
    }
    setBounds(x, y, frameSize.width, frameSize.height);
  }

  /**
   * Create a fallback image for the converter if no other image could be found.
   * 
   * @return the alternative Image.
   */
  private Image createFallbackImage()
  {
    Image image = new BufferedImage(150, 180, BufferedImage.TYPE_BYTE_INDEXED);
    Graphics2D g = (Graphics2D) image.getGraphics();

    // Some text optimization (anti alias)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHints(qualityHints);

    g.setBackground(Color.WHITE);
    g.clearRect(0, 0, 150, 180);
    Font font = new Font("Arial", Font.BOLD, 20);
    g.setFont(font);
    g.setColor(Color.DARK_GRAY);
    g.drawString("Drop-It", 30, 60);

    return image;
  }

  public static void main(String[] args)
  {
    Converter converter = new Converter();
    converter.setVisible(true);
  }
}
