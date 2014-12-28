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

import java.util.List;

import static ch.retorte.sensorsamplor.invoker.SensorInvokerManager.JOB_DATA_SENSORS_IDENTIFIER;
import static ch.retorte.sensorsamplor.invoker.SensorInvokerManager.JOB_DATA_SENSOR_BUS_IDENTIFIER;

/**
 * Invokes measurements of sensors.
 */
public class SensorInvoker implements Job {

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
      throw new JobExecutionException(e);
      // TODO: Logging
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
    sensorBus.send(new ErrorSample(sensorException.getPlatformIdentifier(), sensorException.getSensorType(), sensorException.getMessage()));
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
