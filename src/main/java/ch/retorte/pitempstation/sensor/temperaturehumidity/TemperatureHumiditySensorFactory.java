package ch.retorte.pitempstation.sensor.temperaturehumidity;

import ch.retorte.pitempstation.sensor.temperaturehumidity.am2302.Am2302Sensor;
import ch.retorte.pitempstation.sensor.temperaturehumidity.dummy.DummyTemperatureSensor;

/**
 * Created by nw on 07.12.14.
 */
public class TemperatureHumiditySensorFactory {

  public TemperatureHumiditySensor createSensor() {
    try {
      return new Am2302Sensor(4);
    }
    catch (Throwable t) {
      System.err.println("Was not able to instantiate AM 3202 Sensor class:" + t.getMessage());
    }
     return new DummyTemperatureSensor();
  }
}
