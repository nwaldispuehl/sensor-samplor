package ch.retorte.sensorsamplor.receiver;

import ch.retorte.sensorsamplor.sensor.ErrorSample;
import ch.retorte.sensorsamplor.sensor.Sample;

import java.util.List;

/**
 * Knows what to do with samples from a sensor.
 */
public interface SampleReceiver {

  /**
   * Is called when a new sample was added to the buffer.
   *
   * @param sampleBuffer the complete buffer with all available samples.
   * @param sample the sample which was recently added.
   */
  void processSample(List<Sample> sampleBuffer, Sample sample);

  /**
   * Is called when a sensor exception was added to the buffer.
   *
   * @param sampleBuffer the complete buffer with all available samples.
   * @param errorSample the error which was recently added.
   */
  void processError(List<Sample> sampleBuffer, ErrorSample errorSample);
}
