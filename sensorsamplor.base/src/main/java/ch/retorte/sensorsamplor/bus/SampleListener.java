package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;

import java.util.List;

/**
 * Knows how to react on received sample.
 */
public interface SampleListener {

  void onSampleAdded(List<Sample> sampleBuffer, Sample sample);
}
