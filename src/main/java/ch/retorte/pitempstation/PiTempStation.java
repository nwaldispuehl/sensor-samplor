package ch.retorte.pitempstation;

import ch.retorte.pitempstation.invoker.SensorInvokerManager;
import ch.retorte.pitempstation.invoker.TemperatureHumiditySensorInvoker;
import ch.retorte.pitempstation.receiver.temperaturehumidity.ConsolePrintSampleReceiver;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensor;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensorFactory;

/**
 * Created by nw on 06.12.14.
 */
public class PiTempStation {


  public static void main(String[] args) {
    new PiTempStation().start();
  }

  public void start() {
    ConsolePrintSampleReceiver receiver = new ConsolePrintSampleReceiver();
    TemperatureHumiditySensor sensor = new TemperatureHumiditySensorFactory().createSensor();
    TemperatureHumiditySensorInvoker invoker = new TemperatureHumiditySensorInvoker(receiver, sensor);
    SensorInvokerManager manager = new SensorInvokerManager(invoker);
    manager.start();
  }
}
