package ch.retorte.sensorsamplor;

import ch.retorte.sensorsamplor.invoker.SensorInvokerManager;
import ch.retorte.sensorsamplor.invoker.SensorInvoker;
import ch.retorte.sensorsamplor.receiver.console.ConsolePrintSampleReceiver;
import ch.retorte.sensorsamplor.receiver.file.FileSampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.temperature.TemperatureHumiditySensorFactory;

/**
 * Main program of the pi temp station. A data sampling software written in Java for a DHT22 temperature/humidity sensor on a Raspberry Pi.
 */
public class SensorSamplor {

  private static final String SENSOR_PLATFORM_IDENTIIFER = "myRaspberryPi_01";
  private static final String LOGGING_DIRECTORY = "/var/log/sensor-samplor/";
  private static final int GPIO_PIN = 4;
  private static final int MEASUREMENT_INTERVAL = 10;

  public static void main(String[] args) {
    new SensorSamplor().start();
  }

  public void start() {
    createManager().scheduleIntervals(MEASUREMENT_INTERVAL);
  }

  private SensorInvokerManager createManager() {
    return new SensorInvokerManager(addReceiversTo(createInvoker()));
  }

  private SensorInvoker addReceiversTo(SensorInvoker sensorInvoker) {
    sensorInvoker.registerReceiver(new ConsolePrintSampleReceiver());
    sensorInvoker.registerReceiver(new FileSampleReceiver(LOGGING_DIRECTORY));
    return sensorInvoker;
  }

  private SensorInvoker createInvoker() {
    return new SensorInvoker(createSensor());
  }

  private Sensor createSensor() {
    return new TemperatureHumiditySensorFactory().createSensorOn(SENSOR_PLATFORM_IDENTIIFER, GPIO_PIN);
  }


}
