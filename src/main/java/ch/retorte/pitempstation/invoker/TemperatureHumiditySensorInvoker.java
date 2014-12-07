package ch.retorte.pitempstation.invoker;

import ch.retorte.pitempstation.receiver.SampleReceiver;
import ch.retorte.pitempstation.sensor.SensorException;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensor;


/**
 * Created by nw on 07.12.14.
 */
public class TemperatureHumiditySensorInvoker implements Runnable {

  private SampleReceiver sampleReceiver;
  private TemperatureHumiditySensor sensor;

  public TemperatureHumiditySensorInvoker(SampleReceiver sampleReceiver, TemperatureHumiditySensor sensor) {
    this.sampleReceiver = sampleReceiver;
    this.sensor = sensor;
  }

  @Override
  public void run() {
    try {
      sampleReceiver.processSample(sensor.measure());
    }
    catch (SensorException e) {

    }
  }
}
