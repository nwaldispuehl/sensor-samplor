package ch.retorte.pitempstation.invoker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Schedules the sensor invoker.
 */
public class SensorInvokerManager {

  private static final int INITIAL_DELAY_SECONDS = 1;

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private TemperatureHumiditySensorInvoker invoker;

  public SensorInvokerManager(TemperatureHumiditySensorInvoker invoker) {
    this.invoker = invoker;
  }

  public void scheduleIntervals(int seconds) {
    scheduler.scheduleAtFixedRate(invoker, INITIAL_DELAY_SECONDS, seconds, TimeUnit.SECONDS);
  }

  public void stop() {
    scheduler.shutdown();
  }
}
