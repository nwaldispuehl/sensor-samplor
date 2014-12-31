package ch.retorte.sensorsamplor.sensor.systemstats;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import ch.retorte.sensorsamplor.sensor.TransferSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * A sensor providing the current processor load level and memory consumption of the host platform.
 */
public class SystemStatsSensor implements Sensor {

  public static final String IDENTIFIER = "systemStats";

  private final String platformIdentifier;

  private final Logger log = LoggerFactory.getLogger(SystemStatsSensor.class);

  public SystemStatsSensor(String platformIdentifier) {
    this.platformIdentifier = platformIdentifier;
  }

  @Override
  public Sample measure() throws SensorException {
    double processorLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    log.debug("Performed measurement with values: {}, {}.", processorLoad, usedMemory);
    return new TransferSample(platformIdentifier, IDENTIFIER)
        .addItem("load", processorLoad)
        .addItem("usedMemory", usedMemory);
  }

}
