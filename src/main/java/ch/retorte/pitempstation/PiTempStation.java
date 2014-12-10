package ch.retorte.pitempstation;

import ch.retorte.pitempstation.invoker.SensorInvokerManager;
import ch.retorte.pitempstation.invoker.TemperatureHumiditySensorInvoker;
import ch.retorte.pitempstation.receiver.temperaturehumidity.ConsolePrintSampleReceiver;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensor;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensorFactory;

/**
 * Main program of the pi temp station. A data sampling software written in Java for a DHT22 temperature/humidity sensor on a Raspberry Pi.
 */
public class PiTempStation {

  private static final int GPIO_PIN = 4;
  private static final int MEASUREMENT_INTERVAL = 10;

  public static void main(String[] args) {
    new PiTempStation().start();
  }

  public void start() {
    ConsolePrintSampleReceiver receiver = new ConsolePrintSampleReceiver();
    TemperatureHumiditySensor sensor = new TemperatureHumiditySensorFactory().createSensorOn(GPIO_PIN);
    TemperatureHumiditySensorInvoker invoker = new TemperatureHumiditySensorInvoker(receiver, sensor);
    SensorInvokerManager manager = new SensorInvokerManager(invoker);
    manager.scheduleIntervals(MEASUREMENT_INTERVAL);
  }
}
