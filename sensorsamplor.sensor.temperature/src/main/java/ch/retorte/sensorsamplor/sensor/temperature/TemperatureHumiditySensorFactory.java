package ch.retorte.sensorsamplor.sensor.temperature;

import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorFactory;
import ch.retorte.sensorsamplor.sensor.temperature.am2302.Am2302Sensor;
import ch.retorte.sensorsamplor.sensor.temperature.dummy.DummyTemperatureSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Produces an appropriate temperature/humidity sensor. If that goes wrong, a dummy sensor is created.
 */
public class TemperatureHumiditySensorFactory implements SensorFactory {

  private final Logger log = LoggerFactory.getLogger(TemperatureHumiditySensorFactory.class);

  private static final String GPIO_DATA_PIN = "sensorsamplor.sensor.temperature.gpio_data_pin";

  private int gpioPin;

  public Sensor createSensorFor(String platformIdentifier) {
    try {
      return new Am2302Sensor(platformIdentifier, gpioPin);
    } catch (Throwable t) {
      log.error("Was not able to instantiate AM 3202 Sensor class: {}.", t.getMessage());
    }
    return new DummyTemperatureSensor(platformIdentifier);
  }

  @Override
  public String getIdentifier() {
    return Am2302Sensor.IDENTIFIER;
  }

  @Override
  public Collection<String> getConfigurationKeys() {
    return newArrayList(GPIO_DATA_PIN);
  }

  @Override
  public void setConfigurationValues(Map<String, String> configuration) {
    if (configuration.containsKey(GPIO_DATA_PIN)) {
      gpioPin = Integer.valueOf(configuration.get(GPIO_DATA_PIN));
    }
  }
}

