package ch.retorte.sensorsamplor.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Loads configuration items.
 */
public class ConfigurationLoader {

  private final static String CONFIG_FILE_NAME = "sensor-samplor.properties";

  private static Properties properties = new Properties();

  public ConfigurationLoader() {
    initializeProperties();
  }

  private void initializeProperties() {
    try {
      InputStream resourceAsStream = ConfigurationLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
      properties.load(resourceAsStream);
    } catch (Exception e) {
      throw new RuntimeException("Was not able to load property file in the 'conf' folder: " + CONFIG_FILE_NAME);
    }
  }

  public String getStringProperty(String name) {
   return properties.getProperty(name);
  }

  public int getIntegerProperty(String name) {
    return Integer.valueOf(properties.getProperty(name));
  }

}
