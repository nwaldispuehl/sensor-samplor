package ch.retorte.sensorsamplor.sensor;

import ch.retorte.sensorsamplor.Configurable;
import ch.retorte.sensorsamplor.Identifiable;

/**
 * To be implemented of each sensor plugin. Produces a sensor implementation.
 */
public interface SensorFactory extends Configurable, Identifiable {

  /**
   * Returns the sensor of this implementation.
   */
  Sensor createSensorFor(String platformIdentifier);

}
