package ch.retorte.sensorsamplor.invoker;

import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import com.google.common.annotations.VisibleForTesting;

import java.util.List;

/**
 * Invokes measurements of sensors.
 */
public class SensorInvoker implements Runnable {

  private SensorBus sensorBus;
  private final List<Sensor> sensors;

  public SensorInvoker(SensorBus sensorBus, List<Sensor> sensors) {
    this.sensorBus = sensorBus;
    this.sensors = sensors;
  }

  @Override
  public void run() {
    try {
      invokeSensors();
    }
    catch (Exception e) {
      /* The scheduler stores exceptions instead of instantly reacting to them, so we need to do a little work here. */
      System.err.println(e.getMessage());
      System.exit(0);
    }
  }

  void invokeSensors() {
    for (Sensor sensor : sensors) {
      new Thread(createRunnerWith(sensor)).start();
    }
  }

  @VisibleForTesting
  void process(Sample sample) {
    sensorBus.send(sample);
  }

  @VisibleForTesting
  void processError(SensorException sensorException) {
    sensorBus.send(sensorException);
  }

  @VisibleForTesting
  SensorRunner createRunnerWith(Sensor sensor) {
    return new SensorRunner(sensor);
  }

  @VisibleForTesting
  class SensorRunner implements Runnable {

    private Sensor sensor;

    public SensorRunner(Sensor sensor) {
      this.sensor = sensor;
    }

    @Override
    public void run() {
      invokeSensor(sensor);
    }

    @VisibleForTesting
    void invokeSensor(Sensor sensor) {
      try {
        process(sensor.measure());
      }
      catch (SensorException e) {
        processError(e);
      }
    }
  }


}
