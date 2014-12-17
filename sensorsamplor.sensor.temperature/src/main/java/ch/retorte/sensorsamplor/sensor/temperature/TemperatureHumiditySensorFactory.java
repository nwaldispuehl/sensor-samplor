package ch.retorte.sensorsamplor.sensor.temperature;

import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorFactory;
import ch.retorte.sensorsamplor.sensor.temperature.am2302.Am2302Sensor;
import ch.retorte.sensorsamplor.sensor.temperature.dummy.DummyTemperatureSensor;

/**
 * Produces an appropriate temperature/humidity sensor. If that goes wrong, a dummy sensor is created.
 */
public class TemperatureHumiditySensorFactory implements SensorFactory {

  private int gpioPin;

  public TemperatureHumiditySensorFactory(int gpioPin) {
    this.gpioPin = gpioPin;
  }

  public Sensor createSensorFor(String platformIdentifier) {
    try {
      return new Am2302Sensor(platformIdentifier, gpioPin);
    }
    catch (Throwable t) {
      System.err.println("Was not able to instantiate AM 3202 Sensor class: " + t.getMessage());
    }
     return new DummyTemperatureSensor(platformIdentifier);
  }
}