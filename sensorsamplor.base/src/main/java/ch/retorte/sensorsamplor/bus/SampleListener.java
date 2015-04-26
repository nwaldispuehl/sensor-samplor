package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;

/**
 * Knows how to react on received sample.
 */
public interface SampleListener {

  void onSampleAdded(Sample sample);
}
