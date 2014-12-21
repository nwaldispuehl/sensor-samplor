package ch.retorte.sensorsamplor;

import ch.retorte.sensorsamplor.bus.HazelcastSensorBus;
import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.invoker.SensorInvokerManager;
import ch.retorte.sensorsamplor.invoker.SensorInvoker;
import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.receiver.SampleReceiverManager;
import ch.retorte.sensorsamplor.receiver.console.ConsolePrintSampleReceiverFactory;
import ch.retorte.sensorsamplor.receiver.file.FileSampleReceiverFactory;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorFactory;
import ch.retorte.sensorsamplor.sensor.temperature.TemperatureHumiditySensorFactory;
import ch.retorte.sensorsamplor.configuration.ConfigurationLoader;

import java.util.List;

import static ch.retorte.sensorsamplor.configuration.ConfigurationProperties.*;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Main program of the pi temp station. A data sampling software written in Java for a DHT22 temperature/humidity sensor on a Raspberry Pi.
 */
public class SensorSamplor {

  private ConfigurationLoader configurationLoader;
  private SensorBus sensorBus;
  private List<Sensor> sensors = newArrayList();
  private List<SampleReceiver> receivers = newArrayList();

  public static void main(String[] args) {
    new SensorSamplor().start();
  }

  public void start() {
    loadConfiguration();
    createSensorBus();
    loadReceivers();
    createReceiverManager();
    loadSensors();
    createSensorManager().scheduleIntervals(getMeasurementInterval());
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

  private void createSensorBus() {
    sensorBus = new HazelcastSensorBus(getSensorPlatformIdentifier(), getBusName(), getUsername(), getPassword(), getNetworkInterfaces());
  }

  private void loadSensors() {
    List<String> activeSensor = getActiveSensors();
    for (SensorFactory f : discoverSensors()) {
      if (activeSensor.contains(f.getIdentifier())) {
        sensors.add(f.createSensorFor(getSensorPlatformIdentifier(), sensorBus));
      }
    }
  }

  private List<SensorFactory> discoverSensors() {
    List<SensorFactory> sensorFactories = newArrayList();
    sensorFactories.add(new TemperatureHumiditySensorFactory(getGpioPin()));
    return sensorFactories;
  }

  private void loadReceivers() {
    List<String> activeReceivers = getActiveReceivers();
    for (ReceiverFactory f : discoverReceivers()) {
      if (activeReceivers.contains(f.getIdentifier())) {
        receivers.add(f.createReceiver());
      }
    }
  }

  private List<ReceiverFactory> discoverReceivers() {
    List<ReceiverFactory> receiverFactories = newArrayList();
    receiverFactories.add(new ConsolePrintSampleReceiverFactory());
    receiverFactories.add(new FileSampleReceiverFactory(getLoggingDirectory()));
    return receiverFactories;
  }

  private SampleReceiverManager createReceiverManager() {
    return new SampleReceiverManager(sensorBus, receivers, getReceiverSensorPattern(), getReceiverPlatformPattern());
  }

  private SensorInvokerManager createSensorManager() {
    return new SensorInvokerManager(createSensorInvoker());
  }

  private SensorInvoker createSensorInvoker() {
    return new SensorInvoker(sensorBus, sensors);
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

  private String getBusName() {
    return configurationLoader.getStringProperty(BUS_NAME);
  }

  private String getUsername() {
    return configurationLoader.getStringProperty(BUS_USERNAME);
  }

  private String getPassword() {
    return configurationLoader.getStringProperty(BUS_PASSWORD);
  }

  private List<String> getNetworkInterfaces() {
    return configurationLoader.getStringListProperty(INTERFACES);
  }

  private List<String> getActiveSensors() {
    return configurationLoader.getStringListProperty(ACTIVE_SENSORS);
  }

  private List<String> getActiveReceivers() {
    return configurationLoader.getStringListProperty(ACTIVE_RECEIVERS);
  }

  private String getReceiverSensorPattern() {
    return configurationLoader.getStringProperty(RECEIVER_SENSOR_TYPE_PATTERN);
  }

  private String getReceiverPlatformPattern() {
    return configurationLoader.getStringProperty(RECEIVER_PLATFORM_IDENTIFIER_PATTERN);
  }

}
