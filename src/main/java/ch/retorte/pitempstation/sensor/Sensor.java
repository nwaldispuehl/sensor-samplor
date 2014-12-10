package ch.retorte.pitempstation.sensor;

/**
 * Knows how to obtain a sample.
 */
public interface Sensor {

  /**
   * Performs one single measurement and produces a sample. Or a sensor exception, that is.
   */
  Sample measure() throws SensorException;
}
