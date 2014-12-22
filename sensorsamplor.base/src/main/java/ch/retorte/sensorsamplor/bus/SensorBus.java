package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;

import java.util.List;

/**
 * Backbone of the sample distribution.
 */
public interface SensorBus {

  void send(Sample sample);

  void registerSampleListener(SampleListener sampleListener);

  List<Sample> getBuffer();
}
