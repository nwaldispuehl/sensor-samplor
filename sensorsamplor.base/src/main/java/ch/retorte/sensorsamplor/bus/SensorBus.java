package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;

/**
 * Backbone of the sample distribution.
 */
public interface SensorBus {

  void send(Sample sample);

  void registerSampleListener(SampleListener sampleListener);

}
