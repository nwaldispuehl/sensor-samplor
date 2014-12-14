package ch.retorte.sensorsamplor.receiver.file;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sample;
import com.google.common.annotations.VisibleForTesting;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;

import static java.io.File.separator;

/**
 * Saves samples to a log file carrying the current date as name.
 */
public class FileSampleReceiver implements SampleReceiver {

  public static final String LOG_FILE_PREFIX = "sensor.log.";
  public static final String ERROR_LOG_FILE_PREFIX = "error.log.";

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
  public void processSample(Sample sample) {
    processSampleWithErrorHandling(LOG_FILE_PREFIX, sample);
  }

  @Override
  public void processError(Sample sample) {
    processSampleWithErrorHandling(ERROR_LOG_FILE_PREFIX, sample);
  }

  private void processSampleWithErrorHandling(String prefix, Sample sample) {
    try {
      File logFile = getOrCreateLogFileFor(prefix, sample);
      appendSampleToFile(sample, logFile);
    }
    catch (FileNotFoundException fileNotFoundException) {
      throw new RuntimeException("Not log file found for writing: " + fileNotFoundException.getMessage());
    }
    catch (IOException ioException) {
      throw new RuntimeException("Was not able to create log file: " + ioException.getMessage());
    }
  }

  private void appendSampleToFile(Sample sample, File logFile) throws IOException {
    PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
    printWriter.println(sample);
    printWriter.flush();
    printWriter.close();
  }

  private File getOrCreateLogFileFor(String prefix, Sample sample) throws IOException {
    File logFile = new File(logFilePath + getFileNameForSample(prefix, sample));
    if (!logFile.exists()) {
      logFile.createNewFile();
    }
    return logFile;
  }

  private String getFileNameForSample(String prefix, Sample sample) {
    return prefix + formatDateOf(sample);
  }

  private String formatDateOf(Sample sample) {
    return format(sample.getDate());
  }

  @VisibleForTesting
  String format(DateTime date) {
    return dateFormatter.print(date);
  }


}
