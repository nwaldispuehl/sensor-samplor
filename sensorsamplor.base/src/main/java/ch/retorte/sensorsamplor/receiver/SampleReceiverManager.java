package ch.retorte.sensorsamplor.receiver;

import ch.retorte.sensorsamplor.bus.SampleListener;
import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.SensorException;
import com.google.common.annotations.VisibleForTesting;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Dispatches received samples and decides which samples to filter.
 */
public class SampleReceiverManager {

  private List<SampleReceiver> receivers;
  private SensorBus sensorBus;
  private Pattern sensorPattern;
  private Pattern platformPattern;

  public SampleReceiverManager(SensorBus sensorBus, List<SampleReceiver> receivers, String sensorPatternString, String platformPatternString) {
    this.receivers = receivers;
    this.sensorBus = sensorBus;

    compilePattern(sensorPatternString, platformPatternString);
    registerSampleListener();
  }

  private void compilePattern(String sensorPatternString, String platformPatternString) {
    sensorPattern = Pattern.compile(sensorPatternString);
    platformPattern = Pattern.compile(platformPatternString);
  }

  private void registerSampleListener() {
    sensorBus.registerSampleListener(new SampleListener() {

      @Override
      public void onSampleAdded(List<Sample> sampleBuffer, Sample sample) {
        processSample(sample);
      }
    });
  }

  private void processSample(Sample sample) {
    if (sampleMatches(sample)) {
      for (SampleReceiver receiver : receivers) {
        new Thread(new ReceiverRunner(sample, receiver)).start();
      }
    }
  }

  private boolean sampleMatches(Sample sample) {
    return sensorTypeMatches(sample) && platformIdentifierMatches(sample);
  }

  private boolean sensorTypeMatches(Sample sample) {
    return patternMatches(sensorPattern, sample.getSensorType());
  }

  private boolean platformIdentifierMatches(Sample sample) {
    return patternMatches(platformPattern, sample.getPlatformIdentifier());
  }

  @VisibleForTesting
  boolean patternMatches(Pattern pattern, String stringToMatch) {
    return pattern.matcher(stringToMatch).matches();
  }

  private class ReceiverRunner implements Runnable {

    private Sample sample;
    private SampleReceiver receiver;

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
      if (sample instanceof SensorException) {
        receiver.processError(sensorBus.getBuffer(), (SensorException) sample);
      }
      else {
        receiver.processSample(sensorBus.getBuffer(), sample);
      }
    }
  }
}
