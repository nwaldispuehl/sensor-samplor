package ch.retorte.sensorsamplor.sensor.temperature.dummy;

import ch.retorte.sensorsamplor.sensor.SensorException;
import ch.retorte.sensorsamplor.sensor.temperature.TemperatureHumiditySample;
import ch.retorte.sensorsamplor.sensor.temperature.TemperatureHumiditySensor;

import java.util.Random;

/**
 * This dummy sensor does not rely on any physical sensor, but creates random data. It is suitable for test purposes.
 */
public class DummyTemperatureSensor implements TemperatureHumiditySensor {

  private final Random random = new Random();
  private String platformIdentifier;

  public DummyTemperatureSensor(String platformIdentifier) {
    this.platformIdentifier = platformIdentifier;
  }

  @Override
  public TemperatureHumiditySample measure() throws SensorException {
    if (random.nextDouble() < 0.1) {
      throw new SensorException(platformIdentifier, "Dummy sensor error.");
    }

    return new TemperatureHumiditySample(platformIdentifier, randomTemperature(), randomHumidity());
  }

  private double randomTemperature() {
    return random.nextInt(10) + 20 + random.nextDouble();
  }

  private double randomHumidity() {
    return random.nextInt(60) + 20 + random.nextDouble();
  }


}
