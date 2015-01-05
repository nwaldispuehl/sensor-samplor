package ch.retorte.sensorsamplor.receiver.exporter;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.ErrorSample;
import ch.retorte.sensorsamplor.sensor.Sample;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newConcurrentMap;

/**
 * Exports the last n samples of every sensor into a JSON data structure.
 */
public class JsonExporterSampleReceiver implements SampleReceiver {

  public static final String IDENTIFIER = "json";

  private String outputFile;
  private int maximumEntriesPerSensor;

  /* Keeps the last n sensor items for every node and sensor where n is at most maximumEntriesPerSensor. */
  private static Map<String, Map<String, Map<String, List<SampleTuple>>>> nodeData;

  public JsonExporterSampleReceiver(String outputFile, int maximumEntriesPerSensor) {
    this.outputFile = outputFile;
    this.maximumEntriesPerSensor = maximumEntriesPerSensor;

    initializeSampleStore();
  }

  private void initializeSampleStore() {
    nodeData = newConcurrentMap();
  }

  @Override
  public void processSample(List<Sample> sampleBuffer, Sample sample) {
    checkForNode(sample);
    checkForSensor(sample);
    checkForKey(sample);
  }

  private void checkForNode(Sample sample) {
    if (!nodeData.containsKey(sample.getPlatformIdentifier())) {
      nodeData.put(sample.getPlatformIdentifier(), Maps.<String, Map<String, List<SampleTuple>>>newConcurrentMap());
    }
  }

  private void checkForSensor(Sample sample) {
    if (!nodeData.get(sample.getPlatformIdentifier()).containsKey(sample.getSensorType())) {
      nodeData.get(sample.getPlatformIdentifier()).put(sample.getSensorType(), Maps.<String, List<SampleTuple>>newConcurrentMap());
    }
  }

  private void checkForKey(Sample sample) {

  }

  @Override
  public void processError(List<Sample> sampleBuffer, ErrorSample errorSample) {
    // ignore for now
  }

  private SampleTuple from(DateTime timestamp, Object value) {
    return new SampleTuple(timestamp, value);
  }

  private class SampleTuple {
    public DateTime timestamp;
    public Object value;

    SampleTuple(DateTime timestamp, Object value) {
      this.timestamp = timestamp;
      this.value = value;
    }
  }

}
