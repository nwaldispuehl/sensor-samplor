package ch.retorte.pitempstation.invoker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by nw on 07.12.14.
 */
public class SensorInvokerManager {

  private TemperatureHumiditySensorInvoker invoker;

  public SensorInvokerManager(TemperatureHumiditySensorInvoker invoker) {
    this.invoker = invoker;
  }

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public void start() {
    scheduler.scheduleAtFixedRate(invoker, 1, 10, TimeUnit.SECONDS);
  }

  public void stop() {
    scheduler.shutdown();
  }
}
