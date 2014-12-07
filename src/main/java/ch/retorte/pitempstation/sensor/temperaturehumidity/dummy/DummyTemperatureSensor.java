package ch.retorte.pitempstation.sensor.temperaturehumidity.dummy;

import ch.retorte.pitempstation.sensor.SensorException;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySample;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensor;

import java.util.Random;

/**
 * This dummy sensor does not rely on any physical sensor, but creates random data. It is suitable for test purposes.
 */
public class DummyTemperatureSensor implements TemperatureHumiditySensor {

  private Random random = new Random();

  @Override
  public TemperatureHumiditySample measure() throws SensorException {
    return new TemperatureHumiditySample(random.nextDouble(), random.nextDouble());
  }
}
