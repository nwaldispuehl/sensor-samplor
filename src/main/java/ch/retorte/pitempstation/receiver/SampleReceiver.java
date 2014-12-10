package ch.retorte.pitempstation.receiver;

import ch.retorte.pitempstation.sensor.Sample;

/**
 * Knows what to do with samples from a sensor.
 */
public interface SampleReceiver {

  void processSample(Sample sample);

  void processError(Sample sample);
}
