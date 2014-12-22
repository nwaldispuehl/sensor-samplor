package ch.retorte.sensorsamplor.receiver.file;

import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Map;

import static ch.retorte.sensorsamplor.receiver.file.FileSampleReceiver.IDENTIFIER;

/**
 * Creates a receiver which stores samples in log files.
 */
public class FileSampleReceiverFactory implements ReceiverFactory {

  public static final String LOGGING_DIRECTORY = "sensorsamplor.receiver.logfile.logging_directory";

  private String logFilePath;

  @Override
  public SampleReceiver createReceiver() {
    return new FileSampleReceiver(logFilePath);
  }

  @Override
  public String getIdentifier() {
    return IDENTIFIER;
  }

  @Override
  public Collection<String> getConfigurationKeys() {
    return Lists.newArrayList(LOGGING_DIRECTORY);
  }

  @Override
  public void setConfigurationValues(Map<String, String> configuration) {
    if (configuration.containsKey(LOGGING_DIRECTORY)) {
      logFilePath = configuration.get(LOGGING_DIRECTORY);
    }
  }
}
