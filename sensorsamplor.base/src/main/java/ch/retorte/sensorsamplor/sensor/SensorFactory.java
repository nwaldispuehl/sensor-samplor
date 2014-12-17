package ch.retorte.sensorsamplor.sensor;

/**
 * To be implemented of each sensor plugin. Produces a sensor implementation.
 */
public interface SensorFactory {

  /**
   * Returns the sensor of this implementation.
   */
  Sensor createSensorFor(String platformIdentifier);
}
