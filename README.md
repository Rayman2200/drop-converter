# Drop-Converter

The Drop-Converter is a small tool that helps converting files via drag&drop into a target format depends on the used plugin. Such plugins can be used to encode files to base64 or hex and many more formats depending on the implemented plugin features. 

## Plugins
The converter is based on a simple and easy to learn plugin interface. Own plugins need to be linked against the drop-converter-plugin jar and implementing the `de.drop_converter.plugin.ConverterPlugin` interface. The interface offers advanced plugin configuration and contains many optional methods. A smaller plugin footprint is available via a Adapter class `de.drop_converter.plugin.ConverterPluginAdapter`.  

### Usage
#### Essential
The two main methods that need to be implemented are `public boolean canImport(TransferSupport support)` which check if the plugin can handle the drop and `public boolean importData(TransferSupport support) throws ConverterException` which perform the convert for the given drop.

The TransferSupport object has a method for checking for compatible `DataFlavor`. One of the most common DataFlavor is `DataFlavor.javaFileListFlavor` which represent a `List<File>`. So if the plugin may only support `File` objects, the `canImport(...)` method can look like this:

```java
  public boolean canImport(TransferSupport support)
  {
    return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
  }
```

Now we can handle this drop with the `importData(...)` method which may look like this:
```java
  public boolean importData(TransferSupport support) throws ConverterException
  {
    try 
    {
      Object transferData = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
      List<File> files = (List<File>) transferData;
      for (File file : files) 
      {
        // do something with the file
      }
      return true;
    } catch (UnsupportedFlavorException | IOException e) {
      // do some error handling or throw a ConverterException
      throw new ConverterException(e);
    }
  }
```

Overriding the `public String toString()` offer a simple way to return the plugin name. 

#### Advanced
Each plugin should be described via the `ConverterPluginDetails` Annotation which need to be attached to the class. This may look like:

```java
@ConverterPluginDetails (authorName = "your name", pluginName = "plugin name", pluginVersion = "1.0")
public class Example implements ConverterPlugin { }

```

The Plugin name will be shown in the plugin chooser and should be contain a short name or description. This annotation can be used instead of overriding the `public String toString()`.

For larger plugins it may be useful to initialize the plugin and ressources via the `public void initPlugin()` and `public void enablePlugin()` methods. The `initPlugin()` method initialize the plugin during the converter start and should be used for some common initialization. The `enablePlugin()` method will be triggered if the plugin was selected and should init ressources that are needed for performing a convert. If the user switch the plugin, the `disablePlugin()` method will be triggered and the initialized resources should be cleaned. The converter will trigger `destroyPlugin()` if the user hit the close button, so additional cleanup can be made.

If the plugin need to be configured, a configuration panel can be used to offer the user a gui. This panel can be returned using the `public JPanel getConfigPanel()` method. This JPanel will be shown in the plugin configuration section, which need to be implemented. 

### Example using the full featured interface

```java
import javax.swing.JPanel;
import javax.swing.TransferHandler.TransferSupport;
import de.drop_converter.plugin.ConverterPlugin;
import de.drop_converter.plugin.exception.*;

public class Example implements ConverterPlugin
{
  @Override
  public void initPlugin() throws InitializationException
  {}

  @Override
  public void destroyPlugin() throws InitializationException
  {}

  @Override
  public void enablePlugin() throws InitializationException
  {}

  @Override
  public void disablePlugin() throws InitializationException
  {}

  @Override
  public boolean canImport(TransferSupport support)
  {
    return false;
  }

  @Override
  public boolean importData(TransferSupport support) throws ConverterException
  {
    return false;
  }

  @Override
  public JPanel getConfigPanel()
  {
    return null;
  }
}
```

### Example using the adapter class
```java
import javax.swing.TransferHandler.TransferSupport;
import de.drop_converter.plugin.ConverterPluginAdapter;
import de.drop_converter.plugin.exception.ConverterException;

public class Example2 extends ConverterPluginAdapter
{
  @Override
  public boolean canImport(TransferSupport support)
  {
    return false;
  }

  @Override
  public boolean importData(TransferSupport support) throws ConverterException
  {
    return false;
  }
}
```

## TODO

- [x] Plugins can be added through a drag and drop on the ComboBox.
- [ ] More complex plugin-structure (at this time, a plugin need to have all his dependencies inside the jar)
  - [ ] Subfolders for plugins with dependencies
  - [ ] Zip-container for plugins with dependencies
- [ ] Own classloader for each plugin to prevent dependency conflicts
- [ ] Context menu for converter configuration
- [ ] Settings dialog for plugin configuration
  - [ ] TabbedPanel where each plugin will be displayed via his name
  - [x] Plugins can provide a JPanel for his configuration dialog
- [ ] JNLP version of the drop converter
- [ ] Provide the plugin jar over a maven repository
- [ ] Customization 
  - [ ] Plugins can provide own image for the drop area
  - [ ] Themes (cooperate design)
- [ ] Status- / Progressbar
- [ ] Trayicon for minimization
- [ ] Helper for determinating the output (file in filesystem or to clipboard)
- [ ] Contextmenu extensions for plugins 
