package ch.retorte.sensorsamplor.receiver.exporter;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.ErrorSample;
import ch.retorte.sensorsamplor.sensor.Sample;
import com.google.common.annotations.VisibleForTesting;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static org.joda.time.DateTime.now;

/**
 * Exports the last n samples of every sensor into a JSON data structure.
 */
public class JsonExporterSampleReceiver implements SampleReceiver {

  public static final String IDENTIFIER = "json";

  /* Don't export more than once every n seconds. */
  private static final int EXPORT_INTERVAL_SECONDS = 10;

  private final Logger log = LoggerFactory.getLogger(JsonExporterSampleReceiver.class);

  private static DateTime lastExport = now();

  private static boolean firstRun = true;

  private String outputFile;

  /* Keeps the last n sensor items for every node and sensor where n is at most maximumEntriesPerSensor. */
  private static SampleCollection sampleCollection;

  public JsonExporterSampleReceiver(String outputFile, int maximumEntriesPerSensor) {
    this.outputFile = outputFile;
    initializeSampleCollectionWith(maximumEntriesPerSensor);
  }

  private void initializeSampleCollectionWith(int maximumEntriesPerSensor) {
    sampleCollection = new SampleCollection(maximumEntriesPerSensor);
    log.debug("Initialized JSON sample collection with {} maximum entries per sensor.", maximumEntriesPerSensor);
  }

  @Override
  public void processSample(List<Sample> sampleBuffer, Sample sample) {
    importCompleteBufferIfFirstRun(sampleBuffer);
    addToCollection(sample);
    conditionallyCreateExport();
  }

  private synchronized void importCompleteBufferIfFirstRun(List<Sample> sampleBuffer) {
    if (firstRun) {
      log.debug("First run, adding all {} items already in list.", sampleBuffer.size());
      for (Sample s : sampleBuffer) {
        addToCollection(s);
      }
      firstRun = false;
    }
  }

  private void addToCollection(Sample sample) {
    if (isNoError(sample)) {
      log.debug("Adding sample ({}) to collection.", sample.getId());
      sampleCollection.addSample(sample);
    }
  }

  private boolean isNoError(Sample sample) {
    return !(sample instanceof ErrorSample);
  }

  private synchronized void conditionallyCreateExport() {
    if (enoughTimeHasPassed()) {
      log.debug("Exporting colletion to json file.");
      exportCollectionWithErrorHandling();
    }
  }

  private synchronized boolean enoughTimeHasPassed() {
    return areAtLeastSecondsPassedBetween(getLastExport(), now(), EXPORT_INTERVAL_SECONDS);
  }

  @VisibleForTesting
  boolean areAtLeastSecondsPassedBetween(DateTime t0, DateTime t1, int seconds) {
    return 0 <= t1.compareTo(t0.plusSeconds(seconds));
  }

  private synchronized DateTime getLastExport() {
    return lastExport;
  }

  private void exportCollectionWithErrorHandling() {
    try {
      exportCollection();
      updateLastExportTimestamp();
    }
    catch (IOException e) {
      log.error("Was not able to export JSON to path: {} because of: {}.", outputFile, e.getMessage());
    }
  }

  private synchronized void updateLastExportTimestamp() {
    lastExport = now();
    log.debug("Updated last export time stamp to: {}.", lastExport);
  }

  private void exportCollection() throws IOException {
    String json = sampleCollection.toJSON();
    Files.write(Paths.get(outputFile), json.getBytes(UTF_8));
  }

  @Override
  public void processError(List<Sample> sampleBuffer, ErrorSample errorSample) {
    // ignore for now
  }

}
