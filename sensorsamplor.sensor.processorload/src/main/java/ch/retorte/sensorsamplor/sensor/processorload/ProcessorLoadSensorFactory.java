package ch.retorte.sensorsamplor.sensor.processorload;

import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorFactory;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Creates the processor load sensor.
 */
public class ProcessorLoadSensorFactory implements SensorFactory {

  @Override
  public Sensor createSensorFor(String platformIdentifier) {
    return new ProcessorLoadSensor(platformIdentifier);
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
    return ProcessorLoadSensor.IDENTIFIER;
  }
}
