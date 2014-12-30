package ch.retorte.sensorsamplor.invoker;

import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.sensor.ErrorSample;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import com.google.common.annotations.VisibleForTesting;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ch.retorte.sensorsamplor.invoker.SensorInvokerManager.JOB_DATA_SENSORS_IDENTIFIER;
import static ch.retorte.sensorsamplor.invoker.SensorInvokerManager.JOB_DATA_SENSOR_BUS_IDENTIFIER;

/**
 * Invokes measurements of sensors.
 */
public class SensorInvoker implements Job {

  private final Logger log = LoggerFactory.getLogger(SensorInvoker.class);

  private SensorBus sensorBus;
  private List<Sensor> sensors;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    populateDataFrom(context);

    try {
      invokeSensors();
    }
    catch (Exception e) {
      /* The scheduler stores exceptions instead of instantly reacting to them, so we need to do a little work here. */
      log.error("Problem with sensor invocation: {}.", e.getMessage());
      throw new JobExecutionException(e);
    }
  }

  private void populateDataFrom(JobExecutionContext context) {
    sensorBus = getSensorBusFrom(context.getJobDetail());
    sensors = getSensorsFrom(context.getJobDetail());
  }

  private SensorBus getSensorBusFrom(JobDetail jobDetail) {
    return (SensorBus) getDataFrom(jobDetail, JOB_DATA_SENSOR_BUS_IDENTIFIER);
  }

  private List<Sensor> getSensorsFrom(JobDetail jobDetail) {
    return (List<Sensor>) getDataFrom(jobDetail, JOB_DATA_SENSORS_IDENTIFIER);
  }

  private Object getDataFrom(JobDetail jobDetail, String key) {
    return jobDetail.getJobDataMap().get(key);
  }

  void invokeSensors() {
    for (Sensor sensor : getSensors()) {
      new Thread(createRunnerWith(sensor)).start();
    }
  }

  @VisibleForTesting
  void process(Sample sample) {
    getSensorBus().send(sample);
  }

  @VisibleForTesting
  void processError(SensorException sensorException) {
    log.warn("Sensor produced exception: {}.", sensorException.getMessage());
    getSensorBus().send(new ErrorSample(sensorException.getPlatformIdentifier(), sensorException.getSensorType(), sensorException.getMessage()));
  }

  @VisibleForTesting
  SensorRunner createRunnerWith(Sensor sensor) {
    return new SensorRunner(sensor);
  }

  @VisibleForTesting
  SensorBus getSensorBus() {
    return sensorBus;
  }

  @VisibleForTesting
  List<Sensor> getSensors() {
    return sensors;
  }

  @VisibleForTesting
  class SensorRunner implements Runnable {

    private final Sensor sensor;

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
