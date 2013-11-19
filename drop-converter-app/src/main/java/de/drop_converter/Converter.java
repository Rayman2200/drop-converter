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
import javax.swing.JSeparator;
import javax.swing.UIManager;

import de.drop_converter.components.ContextMenu;
import de.drop_converter.components.JDropableComponent;
import de.drop_converter.components.JPluginComboBox;
import de.drop_converter.components.actions.ExitContext;
import de.drop_converter.components.actions.SettingsContext;

/**
 * Main executable class. Provide the GUI and initialization.
 * 
 * @author Thomas Chojecki
 */
public class Converter extends JFrame
{

  private static final long serialVersionUID = 907314946871184488L;

  /*
   * GUI Components
   */
  private final Position position = Position.LOWER_RIGHT;

  private final JLabel labelTop = new JLabel(" ");

  private final JLabel labelBottom = new JLabel(" ");

  private final JPluginComboBox pluginsChooser;

  private final PluginHandler pluginHandler;

  private final JDropableComponent dropComponent;

  /*
   * Configuration
   */
  private Configuration config;

  public final static File CONVERTER_BASE_DIR = new File(System.getProperty("user.home"), ".drop_converter");

  public final static File CONVERTER_PLUGIN_DIR = new File(CONVERTER_BASE_DIR, "plugins");

  public final static File CONVERTER_LOGGING_DIR = new File(CONVERTER_BASE_DIR, "logging");

  public final static File CONFIGURATION_FILE = new File(CONVERTER_BASE_DIR, "settings.ini");

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
    // init the directory structure for the converter (plugin, logging and
    // configuration directories)
    initDirectories();

    try {
      config = new Configuration(CONFIGURATION_FILE);
    } catch (IOException e1) {
      LOGGER.log(Level.SEVERE, "Could not initialize configuration", e1);
      System.exit(1);
    }

    Image image = null;
    URL resource = null;
    try {
      resource = getClass().getResource("/images/dragdrop-150.png");
      if (resource == null) {
        LOGGER.warning("Could not find drop-image.");
        image = createFallbackImage();
      } else {
        image = ImageIO.read(resource);
      }
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Could not parse drop-image from " + resource + ". Creating fallback image");
      image = createFallbackImage();
    }

    pluginHandler = new PluginHandler();
    pluginsChooser = new JPluginComboBox(pluginHandler);
    dropComponent = new JDropableComponent(image, pluginsChooser);

    // init the gui (all components should be initialized before this call
    initGUI();

    // create the context menu and init the entries
    initContextMenu();

    URLClassLoader pluginClassloader = loadPlugins();

    if (pluginClassloader != null) {
      pluginHandler.loadPlugins(loadPlugins());
      // pluginsChooser.reloadPlugins();
    }

  }

  /**
   * Do some graphical initialization.
   * 
   * @throws IllegalStateException if at least one component that is gui relevant is null.
   */
  private void initGUI() throws IllegalStateException
  {
    setAlwaysOnTop(true);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout());
    setTitle("Converter");
    labelBottom.setBackground(Color.WHITE);
    labelTop.setBackground(Color.WHITE);

    try {
      UIManager.setLookAndFeel(config.getLookAndFeel());
    } catch (Exception e1) {
      LOGGER.log(Level.FINE, "Could not load configured look and feel. Using default one.",e1);
    }

    try {
      Dimension screenSize = getToolkit().getScreenSize();

      add(pluginsChooser, BorderLayout.NORTH);
      add(dropComponent, BorderLayout.CENTER);
      pack();
      // alignment need to be done after pack
      alignWindow(getSize(), screenSize, position);
    } catch (HeadlessException e) {
      LOGGER.severe("Converter can not be run in a headless environment.");
      System.exit(1);
    }
  }

  /**
   * Prepare and if needed create the base converter directory. Usually the main directory will be created in the
   * <code>System.getProperty("user.home")</code>.
   */
  private void initDirectories()
  {
    // check and init the directories, if they currently not exist.
    checkDirectory(CONVERTER_BASE_DIR);
    checkDirectory(CONVERTER_PLUGIN_DIR);
    checkDirectory(CONVERTER_LOGGING_DIR);
  }

  /**
   * Creating context menu.
   */
  private void initContextMenu()
  {
    ContextMenu contextMenu = new ContextMenu();

    contextMenu.addMenuEntry(new SettingsContext(this, pluginHandler));
    contextMenu.addMenuEntry(new JSeparator());
    contextMenu.addMenuEntry(new ExitContext(this));
    dropComponent.setContextMenu(contextMenu);;
  }

  /**
   * Check and init the given directory.
   * 
   * @param directory
   */
  private void checkDirectory(File directory)
  {
    if (!directory.exists()) {
      if (directory.mkdirs()) {
        LOGGER.config("Created directory " + directory.getAbsolutePath());
      } else {
        System.err.println("Could not create directory " + directory.getAbsolutePath());
        LOGGER.severe("Created directory " + directory.getAbsolutePath());
        System.exit(1);
      }
    }

    if (!directory.isDirectory()) {
      LOGGER.severe("Could not create directory " + directory.getAbsolutePath());
      System.exit(1);
    }
    if (!directory.canWrite()) {
      LOGGER.severe("No write permission in directory " + directory.getAbsolutePath());
      System.exit(1);
    }
  }

  /**
   * Load all plugins from the plugin directory
   * 
   * @return a URLClassLoader that contains all find plugins.
   */
  private URLClassLoader loadPlugins()
  {
    FilenameFilter jarFileFilter = new FilenameFilter()
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
    File[] jarFiles = CONVERTER_PLUGIN_DIR.listFiles(jarFileFilter);

    if (jarFiles.length > 0) {
      ArrayList<URL> urls = new ArrayList<URL>();
      for (File pluginFile : jarFiles) {
        try {
          urls.add(pluginFile.toURI().toURL());
        } catch (MalformedURLException e) {
          LOGGER.warning("Could not load Plugin: " + pluginFile.getAbsolutePath());
        }
      }
      return URLClassLoader.newInstance(urls.toArray(new URL[0]));
    }
    return null;
  }

  /**
   * Align the converter window to a specific position on the screen.
   * 
   * @param frameSize
   *          is the size of the converter window
   * @param screenSize
   *          is the size of the screen
   * @param position
   *          is the Position where the windows should be shown.
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

  @Override
  public void dispose()
  {
    if (pluginHandler != null) {
      pluginHandler.dispose();
    }
    super.dispose();
  }
  
  public Configuration getConfiguration()
  {
    return config;
  }

  public void setConfig(Configuration config)
  {
    this.config = config;
  }

  public static void main(String[] args)
  {
    Converter converter = new Converter();
    converter.setVisible(true);
  }
}
