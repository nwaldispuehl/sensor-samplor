package ch.retorte.pitempstation.receiver;

import ch.retorte.pitempstation.sensor.Sample;

/**
 * Created by nw on 07.12.14.
 */
public interface SampleReceiver {

  void processSample(Sample sample);

  void processError(Sample sample);
}
