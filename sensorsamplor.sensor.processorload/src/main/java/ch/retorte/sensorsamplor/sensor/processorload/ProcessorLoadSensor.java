package ch.retorte.sensorsamplor.sensor.processorload;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import ch.retorte.sensorsamplor.sensor.TransferSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * A sensor providing the current processor load level of the host platforms processor.
 */
public class ProcessorLoadSensor implements Sensor {

  public static final String IDENTIFIER = "processorLoad";

  private final String platformIdentifier;

  private final Logger log = LoggerFactory.getLogger(ProcessorLoadSensor.class);

  public ProcessorLoadSensor(String platformIdentifier) {
    this.platformIdentifier = platformIdentifier;
  }

  @Override
  public Sample measure() throws SensorException {
    double processorLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    log.debug("Performed measurement with value: {}.", processorLoad);
    return new TransferSample(platformIdentifier, IDENTIFIER).addItem("load", processorLoad);
  }

}
