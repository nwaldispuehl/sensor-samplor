package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;

import java.util.List;

/**
 * Backbone of the sample distribution.
 */
public interface SensorBus {

  /**
   * Sends a sample into the bus so it is available for all nodes.
   */
  void send(Sample sample);

  /**
   * Registers a sample listener which reacts to send sample events.
   */
  void registerSampleListener(SampleListener sampleListener);

  /**
   * Provides the complete, current in-memory buffer of all recently processed samples.
   */
  List<Sample> getBuffer();

  /**
   * Gracefully stops this nodes operation.
   */
  void stop();
}
