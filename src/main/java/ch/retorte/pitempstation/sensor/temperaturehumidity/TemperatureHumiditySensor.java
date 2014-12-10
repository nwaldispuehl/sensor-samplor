package ch.retorte.pitempstation.sensor.temperaturehumidity;

import ch.retorte.pitempstation.sensor.Sensor;
import ch.retorte.pitempstation.sensor.SensorException;

/**
 * A special kind of sensor which measures temperature and humidity.
 */
public interface TemperatureHumiditySensor extends Sensor {

  TemperatureHumiditySample measure() throws SensorException;

}
