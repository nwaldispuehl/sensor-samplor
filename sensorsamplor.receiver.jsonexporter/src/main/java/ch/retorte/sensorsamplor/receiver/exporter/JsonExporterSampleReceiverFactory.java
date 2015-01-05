package ch.retorte.sensorsamplor.receiver.exporter;

import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;

import java.util.Collection;
import java.util.Map;

import static ch.retorte.sensorsamplor.receiver.exporter.JsonExporterSampleReceiver.IDENTIFIER;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Creates the JSON receiver.
 */
public class JsonExporterSampleReceiverFactory implements ReceiverFactory {

  private static final String OUTPUT_FILE = "sensorsamplor.receiver.json.output_file";
  private static final String MAXIMUM_EXPORT_ENTRIES_PER_SENSOR = "sensorsamplor.receiver.json.maximum_entries_per_sensor";

  private String outputFile;
  private int maximumEntriesPerSensor = 256;

  @Override
  public SampleReceiver createReceiver() {
    return new JsonExporterSampleReceiver(outputFile, maximumEntriesPerSensor);
  }

  @Override
  public Collection<String> getConfigurationKeys() {
    return newArrayList(OUTPUT_FILE, MAXIMUM_EXPORT_ENTRIES_PER_SENSOR);
  }

  @Override
  public void setConfigurationValues(Map<String, String> configuration) {
    if (configuration.containsKey(OUTPUT_FILE)) {
      outputFile = configuration.get(OUTPUT_FILE);
    }
    if (configuration.containsKey(MAXIMUM_EXPORT_ENTRIES_PER_SENSOR)) {
      maximumEntriesPerSensor = Integer.valueOf(configuration.get(MAXIMUM_EXPORT_ENTRIES_PER_SENSOR));
    }
  }

  @Override
  public String getIdentifier() {
    return IDENTIFIER;
  }
}
