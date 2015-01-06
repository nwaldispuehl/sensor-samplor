package ch.retorte.sensorsamplor.receiver.exporter;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.ErrorSample;
import ch.retorte.sensorsamplor.sensor.Sample;
import com.google.common.annotations.VisibleForTesting;
import org.joda.time.DateTime;

import java.util.List;

import static org.joda.time.DateTime.now;

/**
 * Exports the last n samples of every sensor into a JSON data structure.
 */
public class JsonExporterSampleReceiver implements SampleReceiver {

  public static final String IDENTIFIER = "json";

  /* Don't export more than once every n seconds. */
  private static final int EXPORT_INTERVAL_SECONDS = 10;

  private static DateTime lastExport = now();

  private String outputFile;

  /* Keeps the last n sensor items for every node and sensor where n is at most maximumEntriesPerSensor. */
  private static SampleCollection sampleCollection;

  public JsonExporterSampleReceiver(String outputFile, int maximumEntriesPerSensor) {
    this.outputFile = outputFile;
    initializeSampleCollectionWith(maximumEntriesPerSensor);
  }

  private void initializeSampleCollectionWith(int maximumEntriesPerSensor) {
    sampleCollection = new SampleCollection(maximumEntriesPerSensor);
  }

  @Override
  public void processSample(List<Sample> sampleBuffer, Sample sample) {
    addToCollection(sample);
    if (enoughTimeHasPassed()) {
      exportCollection();
    }
  }

  private void addToCollection(Sample sample) {
    sampleCollection.addSample(sample);
  }

  private boolean enoughTimeHasPassed() {
    return areAtLeastSecondsPassedBetween(getLastExport(), now(), EXPORT_INTERVAL_SECONDS);
  }

  @VisibleForTesting
  boolean areAtLeastSecondsPassedBetween(DateTime t0, DateTime t1, int seconds) {
    return 0 < t0.plusSeconds(seconds).compareTo(t1);
  }

  private synchronized DateTime getLastExport() {
    return lastExport;
  }

  private void exportCollection() {
    String json = sampleCollection.toJSON();
    // TODO: Write json string to position provided as argument.
  }

  @Override
  public void processError(List<Sample> sampleBuffer, ErrorSample errorSample) {
    // ignore for now
  }



}
