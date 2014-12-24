package ch.retorte.sensorsamplor.receiver.file;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.SensorException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.util.List;

import static java.io.File.separator;

/**
 * Saves samples to a log file carrying the current date as name.
 */
public class FileSampleReceiver implements SampleReceiver {

  public static final String IDENTIFIER = "logfile";

  public static final String LOG_FILE_PREFIX = "sensor.log";
  public static final String ERROR_LOG_FILE_PREFIX = "error.log";
  private static final String PART_DELIMITER = ".";

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

  private final String logFilePath;

  public FileSampleReceiver(String logFilePath) {
    this.logFilePath = checkForTrailingDelimiter(logFilePath);
    conditionallyCreateLogFilePath();
  }

  private void conditionallyCreateLogFilePath() {
    File f = new File(logFilePath);
    if (!f.exists()) {
      boolean pathCreated = f.mkdirs();
      if (!pathCreated) {
        throw new RuntimeException("Was not able to create log file path: " + logFilePath);
      }
    }
  }

  @VisibleForTesting
  String checkForTrailingDelimiter(String logFilePath) {
    if (!logFilePath.endsWith(separator)) {
      logFilePath = logFilePath + separator;
    }
    return logFilePath;
  }

  @Override
  public void processSample(List<Sample> sampleBuffer, Sample sample) {
    processSampleWithErrorHandling(sample);
  }

  @Override
  public void processError(List<Sample> sampleBuffer, SensorException sensorException) {
    processExceptionWithErrorHandling(sensorException);
  }

  private void processSampleWithErrorHandling(Sample sample) {
    processWithErrorHandling(LOG_FILE_PREFIX, sample.getSensorType(), sample.getTimestamp(), sample.toString());
  }

  private void processExceptionWithErrorHandling(SensorException sensorException) {
    processWithErrorHandling(ERROR_LOG_FILE_PREFIX, sensorException.getSensorType(), sensorException.getTimestamp(), sensorException.toString());
  }

  private void processWithErrorHandling(String prefix, String sensorType, DateTime date, String payload) {
    try {
      File logFile = getOrCreateLogFileFor(prefix, sensorType, date);
      appendSampleToFile(payload, logFile);
    }
    catch (FileNotFoundException fileNotFoundException) {
      throw new RuntimeException("No log file found for writing: " + fileNotFoundException.getMessage());
    }
    catch (IOException ioException) {
      throw new RuntimeException("Was not able to create log file: " + ioException.getMessage());
    }
  }

  private File getOrCreateLogFileFor(String prefix, String sensorType, DateTime date) throws IOException {
    File logFile = new File(logFilePath + getFileNameFor(prefix, sensorType, date));
    if (!logFile.exists()) {
      boolean fileCreated = logFile.createNewFile();
      if (!fileCreated) {
        throw new RuntimeException("Was not able to create log file: " + logFile.getAbsolutePath());
      }
    }
    return logFile;
  }

  private void appendSampleToFile(String payload, File logFile) throws IOException {
    Files.append(payload + LINE_SEPARATOR, logFile, Charsets.UTF_8);
  }

  private String getFileNameFor(String prefix, String sensorType, DateTime date) {
    return prefix + PART_DELIMITER + sensorType + PART_DELIMITER + format(date);
  }

  @VisibleForTesting
  String format(DateTime date) {
    return dateFormatter.print(date);
  }


}
