package ch.retorte.sensorsamplor.configuration;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Loads configuration items.
 */
public class ConfigurationLoader {

  private final Logger log = LoggerFactory.getLogger(ConfigurationLoader.class);

  private final static String LIST_DELIMITER = ",";

  private final static String SYSTEM_CONFIG_DIRECTORY = "/etc/sensor-samplor/";

  private final static String CONFIG_FILE_NAME = "sensor-samplor.properties";

  private static Properties properties;

  public ConfigurationLoader() {
    initializeProperties();
  }

  public ConfigurationLoader(Properties properties) {
    this.properties = properties;
  }

  private void initializeProperties() {
    try {
      InputStream resourceAsStream = ConfigurationLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
      properties = new Properties();
      properties.load(resourceAsStream);
      log.debug("Loaded configuration file: {}.", CONFIG_FILE_NAME);
    } catch (Exception e) {
      throw new RuntimeException("Was not able to load property file in the 'conf' folder: " + CONFIG_FILE_NAME);
    }
  }

  /**
   * Gets the configuration string value with the given key name.
   *
   * @param name the key the property is to be found with.
   * @return the value of the property, or null if none was found.
   */
  public String getStringProperty(String name) {
   return properties.getProperty(name);
  }

  /**
   * Returns a list of strings of comma-separated values.
   *
   * @param name the key the property is to be found with.
   * @return a list of values (might be empty) or null if no key with this name was found.
   */
  public List<String> getStringListProperty(String name) {
    String value = getStringProperty(name);
    return getStringListPropertyOf(value);
  }

  @VisibleForTesting
  List<String> getStringListPropertyOf(String value) {
    if (value == null) {
      return null;
    }

    if (value.isEmpty()) {
      return newArrayList();
    }

    List<String> result = newArrayList();
    for (String singleItem : value.split(LIST_DELIMITER)) {
      result.add(singleItem.trim());
    }
    return result;
  }

  /**
   * Gets the configuration integer value with the given key name.
   *
   * @param name the key the property is to be found with.
   * @return the value of the property, or null if none was found.
   */
  public int getIntegerProperty(String name) {
    return Integer.valueOf(properties.getProperty(name));
  }

}
