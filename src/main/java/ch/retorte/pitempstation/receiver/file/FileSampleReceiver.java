package ch.retorte.pitempstation.receiver.file;

import ch.retorte.pitempstation.receiver.SampleReceiver;
import ch.retorte.pitempstation.sensor.Sample;

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
