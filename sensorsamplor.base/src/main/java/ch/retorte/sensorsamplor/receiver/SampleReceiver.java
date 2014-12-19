package ch.retorte.sensorsamplor.receiver;

import ch.retorte.sensorsamplor.bus.SampleListener;
import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.SensorException;

/**
 * Knows what to do with samples from a sensor.
 */
public abstract class SampleReceiver {

  protected SampleReceiver(SensorBus sensorBus) {
    sensorBus.registerSampleListener(new SampleListener() {

      @Override
      public void onSampleAdded(Sample sample) {
        processSample(sample);
      }
    });
  }

  protected abstract void processSample(Sample sample);

  protected abstract void processError(SensorException sensorException);
}
