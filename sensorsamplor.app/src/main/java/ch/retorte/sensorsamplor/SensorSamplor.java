package ch.retorte.sensorsamplor;

import ch.retorte.sensorsamplor.invoker.SensorInvokerManager;
import ch.retorte.sensorsamplor.invoker.SensorInvoker;
import ch.retorte.sensorsamplor.receiver.console.ConsolePrintSampleReceiver;
import ch.retorte.sensorsamplor.receiver.file.FileSampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.temperature.TemperatureHumiditySensorFactory;
import org.apache.commons.cli.Options;

/**
 * Main program of the pi temp station. A data sampling software written in Java for a DHT22 temperature/humidity sensor on a Raspberry Pi.
 */
public class SensorSamplor {

  private static final String IDENTIFIER_SHORT_OPTION = "i";
  private static final String IDENTIFIER_LONG_OPTION = "identifier";
//  private static final String GPIO_PIN_SHORT_OPTION = "p";
//  private static final String GPIO_PIN_LONG_OPTION = "pin";
  private static final String GPIO_PIN_SHORT_OPTION = "p";
  private static final String GPIO_PIN_LONG_OPTION = "pin";

  /* The fields also hold the respective default values. */
  private String sensorPlatformIdentifier = "myRaspberryPi_01";
  private String loggingDirectory = "/var/log/sensor-samplor/";
  private int gpioPin = 4;
  private int measurementInterval = 60;

  private Options cliOptions;

  public static void main(String[] args) {
    new SensorSamplor().startWith(args);
  }

  public void startWith(String[] args) {
    createCliOptions();

    createManager().scheduleIntervals(measurementInterval);
  }

  private void createCliOptions() {
    cliOptions = new Options();
    cliOptions.addOption(IDENTIFIER_SHORT_OPTION, IDENTIFIER_LONG_OPTION, false, "The node identifier.");
    cliOptions.addOption(GPIO_PIN_SHORT_OPTION, GPIO_PIN_LONG_OPTION, false, "The GPIO pin the sensor is attached to.");
  }

  private SensorInvokerManager createManager() {
    return new SensorInvokerManager(addReceiversTo(createInvoker()));
  }

  private SensorInvoker addReceiversTo(SensorInvoker sensorInvoker) {
    sensorInvoker.registerReceiver(new ConsolePrintSampleReceiver());
//    sensorInvoker.registerReceiver(new FileSampleReceiver(loggingDirectory));
    return sensorInvoker;
  }

  private SensorInvoker createInvoker() {
    return new SensorInvoker(createSensor());
  }

  private Sensor createSensor() {
    return new TemperatureHumiditySensorFactory(gpioPin).createSensorFor(sensorPlatformIdentifier);
  }


}
