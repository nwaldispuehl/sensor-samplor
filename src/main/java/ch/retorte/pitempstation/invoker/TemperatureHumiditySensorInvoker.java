package ch.retorte.pitempstation.invoker;

import ch.retorte.pitempstation.receiver.SampleReceiver;
import ch.retorte.pitempstation.sensor.SensorException;
import ch.retorte.pitempstation.sensor.temperaturehumidity.ErrorSample;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensor;


/**
 * Invokes measurements on a temperature/humidity sensor.
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
      sampleReceiver.processError(new ErrorSample(e.getMessage()));
    }
  }
}
