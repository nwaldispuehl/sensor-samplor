package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;

/**
 * @author: nw.
 */
public interface SampleListener {

  void onSampleAdded(Sample sample);
}
