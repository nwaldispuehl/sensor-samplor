package ch.retorte.sensorsamplor.receiver.file;

import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;

import static ch.retorte.sensorsamplor.receiver.file.FileSampleReceiver.IDENTIFIER;

/**
 * Creates a receiver which stores samples in log files.
 */
public class FileSampleReceiverFactory implements ReceiverFactory {

  private String logFilePath;

  public FileSampleReceiverFactory(String logFilePath) {
    this.logFilePath = logFilePath;
  }

  @Override
  public SampleReceiver createReceiver() {
    return new FileSampleReceiver(logFilePath);
  }

  @Override
  public String getIdentifier() {
    return IDENTIFIER;
  }
}
