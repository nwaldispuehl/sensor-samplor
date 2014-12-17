package ch.retorte.sensorsamplor;

import ch.retorte.sensorsamplor.invoker.SensorInvokerManager;
import ch.retorte.sensorsamplor.invoker.SensorInvoker;
import ch.retorte.sensorsamplor.receiver.console.ConsolePrintSampleReceiver;
import ch.retorte.sensorsamplor.receiver.file.FileSampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.temperature.TemperatureHumiditySensorFactory;
import ch.retorte.sensorsamplor.utils.ConfigurationLoader;

import static ch.retorte.sensorsamplor.utils.ConfigurationProperties.*;

/**
 * Main program of the pi temp station. A data sampling software written in Java for a DHT22 temperature/humidity sensor on a Raspberry Pi.
 */
public class SensorSamplor {

  ConfigurationLoader configurationLoader;

  public static void main(String[] args) {
    new SensorSamplor().start();
  }

  public void start() {
    loadConfiguration();
    createManager().scheduleIntervals(getMeasurementInterval());
  }

  private void loadConfiguration() {
    try {
      configurationLoader = new ConfigurationLoader();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(0);
    }
  }

  private SensorInvokerManager createManager() {
    return new SensorInvokerManager(addReceiversTo(createInvoker()));
  }

  private SensorInvoker addReceiversTo(SensorInvoker sensorInvoker) {
//    sensorInvoker.registerReceiver(new ConsolePrintSampleReceiver());
    sensorInvoker.registerReceiver(new FileSampleReceiver(getLoggingDirectory()));
    return sensorInvoker;
  }

  private SensorInvoker createInvoker() {
    return new SensorInvoker(createSensor());
  }

  private Sensor createSensor() {
    return new TemperatureHumiditySensorFactory(getGpioPin()).createSensorFor(getSensorPlatformIdentifier());
  }

  private int getMeasurementInterval() {
    return configurationLoader.getIntegerProperty(MEASUREMENT_INTERVAL);
  }

  private String getLoggingDirectory() {
    return configurationLoader.getStringProperty(LOGGING_DIRECTORY);
  }

  private int getGpioPin() {
    return configurationLoader.getIntegerProperty(GPIO_DATA_PIN);
  }

  private String getSensorPlatformIdentifier() {
    return configurationLoader.getStringProperty(SENSOR_PLATFORM_IDENTIFIER);
  }

}
