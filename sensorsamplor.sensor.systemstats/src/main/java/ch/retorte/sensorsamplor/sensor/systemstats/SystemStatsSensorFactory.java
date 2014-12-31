package ch.retorte.sensorsamplor.sensor.systemstats;

import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorFactory;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Creates the system stats sensor.
 */
public class SystemStatsSensorFactory implements SensorFactory {

  @Override
  public Sensor createSensorFor(String platformIdentifier) {
    return new SystemStatsSensor(platformIdentifier);
  }

  @Override
  public Collection<String> getConfigurationKeys() {
    return newArrayList();
  }

  @Override
  public void setConfigurationValues(Map<String, String> configuration) {
    // nop
  }

  @Override
  public String getIdentifier() {
    return SystemStatsSensor.IDENTIFIER;
  }
}
