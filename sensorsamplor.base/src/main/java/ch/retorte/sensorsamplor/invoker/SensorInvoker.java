package ch.retorte.sensorsamplor.invoker;

import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import com.google.common.annotations.VisibleForTesting;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


/**
 * Invokes measurements of sensors.
 */
public class SensorInvoker implements Runnable {

  private final List<SampleReceiver> sampleReceivers = newArrayList();
  private final Sensor sensor;
  private SensorBus sensorBus;

  public SensorInvoker(Sensor sensor, SensorBus sensorBus) {
    this.sensor = sensor;
    this.sensorBus = sensorBus;
  }

  public void registerReceiver(SampleReceiver sampleReceiver) {
    sampleReceivers.add(sampleReceiver);
  }


  @Override
  public void run() {
    try {
      invokeSensor();
    }
    catch (Exception e) {
      /* The scheduler stores exceptions instead of instantly reacting to them, so we need to do a little work here. */
      System.err.println(e.getMessage());
      System.exit(0);
    }

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
    sensorBus.send(sample);
//    for (SampleReceiver r : sampleReceivers) {
//      r.processSample(sample);
//    }
  }

  @VisibleForTesting
  void processError(SensorException sensorException) {
    for (SampleReceiver r : sampleReceivers) {
      r.processError(sensorException);
    }
  }
}
