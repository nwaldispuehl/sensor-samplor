package ch.retorte.sensorsamplor.invoker;

import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.sensor.Sensor;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.core.jmx.JobDataMapSupport.newJobDataMap;

/**
 * Schedules the sensor invoker.
 */
public class SensorInvokerManager {

  private static final String QUARTZ_CONFIGURATION_FILE = "quartz-scheduler.properties";

  public static final String JOB_DATA_SENSORS_IDENTIFIER = "sensors";
  public static final String JOB_DATA_SENSOR_BUS_IDENTIFIER = "sensorBus";

  private final Logger log = LoggerFactory.getLogger(SensorInvokerManager.class);

  private Scheduler scheduler;
  private JobDetail job;

  public SensorInvokerManager(SensorBus sensorBus, List<Sensor> sensors) throws SchedulerException {
    createScheduler();
    createJobWith(sensorBus, sensors);
  }

  private void createScheduler() throws SchedulerException {
    scheduler = new StdSchedulerFactory(QUARTZ_CONFIGURATION_FILE).getScheduler();
    log.info("Created Quartz scheduler with configuration file: {}.", QUARTZ_CONFIGURATION_FILE);
    scheduler.start();
    log.info("Started Quartz scheduler.");
  }

  private void createJobWith(SensorBus sensorBus, List<Sensor> sensors) {
    job = newJob(SensorInvoker.class).usingJobData(newJobDataMap(mapWith(sensorBus, sensors))).build();
  }

  private Map<String, Object> mapWith(SensorBus sensorBus, List<Sensor> sensors) {
    Map<String, Object> map = newHashMap();
    map.put(JOB_DATA_SENSOR_BUS_IDENTIFIER, sensorBus);
    map.put(JOB_DATA_SENSORS_IDENTIFIER, sensors);
    return map;
  }

  public void scheduleIntervals(String cronExpression) throws SchedulerException {
    scheduler.scheduleJob(job, triggerWith(cronExpression));
    log.info("Scheduled Quartz job with cron expression: {}.", cronExpression);
  }

  private CronTrigger triggerWith(String cronExpression) {
    return newTrigger().startNow().withSchedule(cronSchedule(cronExpression)).build();
  }

  public void stop() throws SchedulerException {
    scheduler.shutdown();
  }
}
