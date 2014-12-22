package ch.retorte.sensorsamplor;

import java.util.Collection;
import java.util.Map;

/**
 * Denotes configurable classes.
 */
public interface Configurable {

  Collection<String> getConfigurationKeys();

  void setConfigurationValues(Map<String, String> configuration);
}
