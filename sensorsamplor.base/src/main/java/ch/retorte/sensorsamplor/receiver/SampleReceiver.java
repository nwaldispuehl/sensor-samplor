package ch.retorte.sensorsamplor.receiver;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.SensorException;

/**
 * Knows what to do with samples from a sensor.
 */
public interface SampleReceiver {

  void processSample(Sample sample);

  void processError(SensorException sensorException);
}
