package ch.retorte.pitempstation.sensor.temperaturehumidity;

import ch.retorte.pitempstation.sensor.Sensor;
import ch.retorte.pitempstation.sensor.SensorException;

/**
 * Created by nw on 07.12.14.
 */
public interface TemperatureHumiditySensor extends Sensor {

  TemperatureHumiditySample measure() throws SensorException;

}
