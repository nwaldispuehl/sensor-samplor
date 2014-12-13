package ch.retorte.sensorsamplor.receiver.file;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sample;

/**
 * Saves samples to a log file carrying the current date as name.
 */
public class FileSampleReceiver implements SampleReceiver {

  private String logFilePath;

  public FileSampleReceiver(String logFilePath) {
    this.logFilePath = logFilePath;
  }

  @Override
  public void processSample(Sample sample) {
    // TODO
  }

  @Override
  public void processError(Sample sample) {
    // TODO
  }
}
