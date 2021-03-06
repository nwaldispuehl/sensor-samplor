package ch.retorte.sensorsamplor.receiver;

import ch.retorte.sensorsamplor.bus.SampleListener;
import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.sensor.ErrorSample;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.SamplePatternChecker;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Dispatches received samples and decides which samples to filter.
 */
public class SampleReceiverManager {

  private final Logger log = LoggerFactory.getLogger(SampleReceiverManager.class);

  private final List<SampleReceiver> receivers;
  private final SensorBus sensorBus;
  private SamplePatternChecker samplePatternChecker;

  public SampleReceiverManager(SensorBus sensorBus, List<SampleReceiver> receivers, String sensorPatternString, String platformPatternString) {
    this.receivers = receivers;
    this.sensorBus = sensorBus;

    samplePatternChecker = new SamplePatternChecker(sensorPatternString, platformPatternString);

    registerSampleListener();

    log.info("Created sample receiver manager with sensor pattern: '{}' and platform pattern: '{}'.", sensorPatternString, platformPatternString);
  }

  private void registerSampleListener() {
    sensorBus.registerSampleListener(new SampleListener() {

      @Override
      public void onSampleAdded(Sample sample) {
        processSample(sample);
      }
    });
  }

  private void processSample(Sample sample) {
    if (samplePatternChecker.matches(sample)) {
      for (SampleReceiver receiver : receivers) {
        new Thread(new ReceiverRunner(sample, receiver)).start();
      }
    }
  }

  private class ReceiverRunner implements Runnable {

    private final Sample sample;
    private final SampleReceiver receiver;

    public ReceiverRunner(Sample sample, SampleReceiver receiver) {
      this.sample = sample;
      this.receiver = receiver;
    }

    @Override
    public void run() {
      invokeSampleReceiver(sample, receiver);
    }

    @VisibleForTesting
    void invokeSampleReceiver(Sample sample, SampleReceiver receiver) {
      log.debug("Invoking sample receiver: {} for sample: {}.", receiver.getClass().getSimpleName(), sample.getId());

      if (sample instanceof ErrorSample) {
        receiver.processError(sensorBus.getBuffer(), (ErrorSample) sample);
      }
      else {
        receiver.processSample(sensorBus.getBuffer(), sample);
      }
    }
  }
}
