package ch.retorte.sensorsamplor.sensor;

import ch.retorte.sensorsamplor.Identifiable;
import ch.retorte.sensorsamplor.bus.SensorBus;

/**
 * To be implemented of each sensor plugin. Produces a sensor implementation.
 */
public interface SensorFactory extends Identifiable {

  /**
   * Returns the sensor of this implementation.
   */
  Sensor createSensorFor(String platformIdentifier, SensorBus sensorBus);

}
