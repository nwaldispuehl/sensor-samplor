package ch.retorte.sensorsamplor.receiver;

import ch.retorte.sensorsamplor.sensor.Sample;

/**
 * Knows what to do with samples from a sensor.
 */
public interface SampleReceiver {

  void processSample(Sample sample);

  void processError(Sample sample);
}
