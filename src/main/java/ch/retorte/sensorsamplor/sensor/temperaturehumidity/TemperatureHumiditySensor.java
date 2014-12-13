package ch.retorte.sensorsamplor.sensor.temperaturehumidity;

import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;

/**
 * A special kind of sensor which measures temperature and humidity.
 */
public interface TemperatureHumiditySensor extends Sensor {

  TemperatureHumiditySample measure() throws SensorException;

}
