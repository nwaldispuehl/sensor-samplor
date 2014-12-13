package ch.retorte.pitempstation.invoker;

import ch.retorte.pitempstation.receiver.SampleReceiver;
import ch.retorte.pitempstation.sensor.Sample;
import ch.retorte.pitempstation.sensor.Sensor;
import ch.retorte.pitempstation.sensor.SensorException;
import ch.retorte.pitempstation.sensor.temperaturehumidity.ErrorSample;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


/**
 * Invokes measurements on a temperature/humidity sensor.
 */
public class SensorInvoker implements Runnable {

  private final List<SampleReceiver> sampleReceivers = newArrayList();
  private final Sensor sensor;

  public SensorInvoker(Sensor sensor) {
    this.sensor = sensor;
  }

  public void registerReceiver(SampleReceiver sampleReceiver) {
    sampleReceivers.add(sampleReceiver);
  }

  @Override
  public void run() {
    invokeSensor();
  }

  @VisibleForTesting
  void invokeSensor() {
    try {
      process(sensor.measure());
    }
    catch (SensorException e) {
      processError(e);
    }
  }

  @VisibleForTesting
  void process(Sample sample) {
    for (SampleReceiver r : sampleReceivers) {
      r.processSample(sample);
    }
  }

  @VisibleForTesting
  void processError(SensorException sensorException) {
    ErrorSample errorSample = new ErrorSample(sensorException.getMessage());
    for (SampleReceiver r : sampleReceivers) {
      r.processError(errorSample);
    }
  }
}
