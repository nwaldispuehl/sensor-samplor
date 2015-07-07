package ch.retorte.sensorsamplor;

import ch.retorte.sensorsamplor.bus.HazelcastSensorBus;
import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.invoker.SensorInvokerManager;
import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.receiver.SampleReceiverManager;
import ch.retorte.sensorsamplor.receiver.console.ConsolePrintSampleReceiverFactory;
import ch.retorte.sensorsamplor.receiver.exporter.JsonExporterSampleReceiverFactory;
import ch.retorte.sensorsamplor.receiver.file.FileSampleReceiverFactory;
import ch.retorte.sensorsamplor.receiver.graphite.GraphiteSampleReceiverFactory;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorFactory;
import ch.retorte.sensorsamplor.sensor.systemstats.SystemStatsSensorFactory;
import ch.retorte.sensorsamplor.sensor.temperature.TemperatureHumiditySensorFactory;
import ch.retorte.sensorsamplor.configuration.ConfigurationLoader;
import ch.retorte.sensorsamplor.sensor.httpxml.HttpXmlSensorFactory;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.retorte.sensorsamplor.configuration.ConfigurationProperties.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Main program of the sensor samplor, a generic data sampling software.
 */
public class SensorSamplor {

  private final Logger log = LoggerFactory.getLogger(SensorSamplor.class);

  private final List<Sensor> sensors = newArrayList();
  private final List<SampleReceiver> receivers = newArrayList();

  private ConfigurationLoader configurationLoader;
  private SensorBus sensorBus;
  private SensorInvokerManager sensorInvokerManager;

  public static void main(String[] args) {
    new SensorSamplor().start();
  }

  public void start() {
    log.info("### Starting SensorSamplor.");

    loadConfiguration();
    createSensorBus();
    loadReceivers();
    createReceiverManager();
    loadSensors();
    scheduleSensors();
    registerShutdownHook();

    log.info("### SensorSamplor operational.");
  }

  private void loadConfiguration() {
    log.info("Loading configuration.");
    try {
      configurationLoader = new ConfigurationLoader();
    }
    catch (Exception e) {
      log.error("Failed to load configuration: {}", e.getMessage());
      System.exit(0);
    }
  }

  private void createSensorBus() {
    log.info("Creating sensor bus.");
    sensorBus = new HazelcastSensorBus(getSensorPlatformIdentifier(), getBusName(), getUsername(), getPassword(), getNetworkInterfaces(), getRemoteMembers());
  }

  private void loadSensors() {
    log.info("Loading sensors.");
    List<String> activeSensor = getActiveSensors();
    for (SensorFactory f : discoverSensors()) {
      if (activeSensor.contains(f.getIdentifier())) {
        log.info("Loading sensor: {}.", f.getIdentifier());
        sensors.add(f.createSensorFor(getSensorPlatformIdentifier()));
      }
    }
  }

  private List<SensorFactory> discoverSensors() {
    List<SensorFactory> sensorFactories = newArrayList();
    sensorFactories.add(new TemperatureHumiditySensorFactory());
    sensorFactories.add(new SystemStatsSensorFactory());
    sensorFactories.add(new HttpXmlSensorFactory());

    configure(sensorFactories);
    log.info("Discovered sensors: {}", sensorFactories);
    return sensorFactories;
  }

  private void scheduleSensors() {
    log.info("Scheduling sensors.");
    try {
      createSensorManager().scheduleIntervals(getMeasurementCronExpression());
    } catch (SchedulerException e) {
      log.error("Was not able to register scheduler: {}.", e.getMessage());
      System.exit(1);
    }
  }

  private void loadReceivers() {
    log.info("Loading receivers.");
    List<String> activeReceivers = getActiveReceivers();
    for (ReceiverFactory f : discoverReceivers()) {
      if (activeReceivers.contains(f.getIdentifier())) {
        log.info("Loading receiver: {}.", f.getIdentifier());
        receivers.add(f.createReceiver());
      }
    }
  }

  private List<ReceiverFactory> discoverReceivers() {
    List<ReceiverFactory> receiverFactories = newArrayList();
    receiverFactories.add(new ConsolePrintSampleReceiverFactory());
    receiverFactories.add(new FileSampleReceiverFactory());
    receiverFactories.add(new JsonExporterSampleReceiverFactory());
    receiverFactories.add(new GraphiteSampleReceiverFactory());

    configure(receiverFactories);
    log.info("Discovered receivers: {}", receiverFactories);
    return receiverFactories;
  }

  private void configure(List<? extends Configurable> configurables) {
    for (Configurable c : configurables) {
      c.setConfigurationValues(getConfigurationItemsFor(c.getConfigurationKeys()));
    }
  }

  private Map<String, String> getConfigurationItemsFor(Collection<String> keys) {
    Map<String, String> result = newHashMap();
    for (String key : keys) {
      String value = configurationLoader.getStringProperty(key);
      if (value != null) {
        result.put(key, value);
      }
    }
    return result;
  }

  private void createReceiverManager() {
    log.info("Creating receiver manager.");
    new SampleReceiverManager(sensorBus, receivers, getReceiverSensorPattern(), getReceiverPlatformPattern());
  }

  private SensorInvokerManager createSensorManager() throws SchedulerException {

    sensorInvokerManager = new SensorInvokerManager(sensorBus, sensors);
    return sensorInvokerManager;
  }

  private void registerShutdownHook() {
    log.info("Registering shutdown hooks.");
    Runtime.getRuntime().addShutdownHook(new SensorSamplorShutdownHook());
  }

  private class SensorSamplorShutdownHook extends Thread {

    @Override
    public void run() {
      stopInvokerManager();
      stopSensorBus();
      log.info("Gracefully shut down SensorSamplor. Have a nice day, sir!");
    }

    private void stopInvokerManager() {
      try {
        sensorInvokerManager.stop();
      } catch (SchedulerException e) {
        log.warn("Was not able to cleanly shut down scheduler. Reason: {}.", e.getMessage());
      }
    }

    private void stopSensorBus() {
      sensorBus.stop();
    }
  }

  private String getMeasurementCronExpression() {
    return configurationLoader.getStringProperty(MEASUREMENT_CRON_EXPRESSION);
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

  private List<String> getRemoteMembers() {
    return configurationLoader.getStringListProperty(REMOTE_MEMBERS);
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
