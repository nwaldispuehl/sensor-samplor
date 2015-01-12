package ch.retorte.sensorsamplor.receiver.exporter;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.utils.SampleDateFormatter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.*;

import static com.google.common.collect.Maps.newConcurrentMap;

/**
 * Holds a series of samples.
 */
public class SampleCollection {

  private int maximumEntriesPerSensor;

  /* Map of nodes, of sensors, of keys, tree map of value tuples.  */
  private Map<String, Map<String, Map<String, TreeMap<SampleTuple, String>>>> data = newConcurrentMap();

  public SampleCollection(int maximumEntriesPerSensor) {
    this.maximumEntriesPerSensor = maximumEntriesPerSensor;
  }

  public synchronized void addSample(Sample sample) {
    checkForDataStructure(sample);
    addDataOf(sample);
  }

  private void checkForDataStructure(Sample sample) {
    if (!hasNode(sample)) {
      createNodeFor(sample);
      createSensorFor(sample);
      createValuesFor(sample);
    }
    else if (!hasSensor(sample)) {
      createSensorFor(sample);
      createValuesFor(sample);
    }
    else if (!hasValues(sample)) {
      createValuesFor(sample);
    }
  }

  private boolean hasNode(Sample sample) {
    return hasNode(sample.getPlatformIdentifier());
  }

  private boolean hasNode(String node) {
    return getNode(node) != null;
  }

  private Map<String, Map<String, TreeMap<SampleTuple, String>>> getNode(Sample sample) {
    return getNode(sample.getPlatformIdentifier());
  }

  private Map<String, Map<String, TreeMap<SampleTuple, String>>> getNode(String node) {
    return data.get(node);
  }

  private void createNodeFor(Sample sample) {
    data.put(sample.getPlatformIdentifier(), Maps.<String, Map<String, TreeMap<SampleTuple, String>>>newConcurrentMap());
  }

  private boolean hasSensor(Sample sample) {
    return hasSensor(sample.getPlatformIdentifier(), sample.getSensorType());
  }

  private boolean hasSensor(String node, String sensor) {
    return getSensor(node, sensor) != null;
  }

  private Map<String, TreeMap<SampleTuple, String>> getSensorFor(Sample sample) {
    return getSensor(sample.getPlatformIdentifier(), sample.getSensorType());
  }

  private Map<String, TreeMap<SampleTuple, String>> getSensor(String node, String sensor) {
    return getNode(node).get(sensor);
  }

  private boolean hasValues(Sample sample) {
    return hasValues(sample.getPlatformIdentifier(), sample.getSensorType(), sample.getData().keySet());
  }

  private boolean hasValues(String node, String sensor, Collection<String> values) {
    for (String value : values) {
      if (!getSensor(node, sensor).containsKey(value)) {
        return false;
      }
    }
    return true;
  }

  private void createSensorFor(Sample sample) {
    getNode(sample).put(sample.getSensorType(), Maps.<String, TreeMap<SampleTuple, String>>newConcurrentMap());
  }

  private void createValuesFor(Sample sample) {
    createValuesFor(sample.getPlatformIdentifier(), sample.getSensorType(), sample.getData().keySet());
  }

  private void createValuesFor(String node, String sensor, Collection<String> values) {
    Map<String, TreeMap<SampleTuple, String>> valuesMap = getSensor(node, sensor);
    for (String v : values) {
      valuesMap.put(v, Maps.<SampleTuple, String>newTreeMap());
    }
  }

  private void addDataOf(Sample sample) {
    Map<String, TreeMap<SampleTuple, String>> sensor = getSensorFor(sample);
    addSampleToSensor(sample, sensor);
  }

  private void addSampleToSensor(Sample sample, Map<String, TreeMap<SampleTuple, String>> sensor) {
    for (Map.Entry<String, Serializable> e : sample.getData().entrySet()) {
      addSampleToTuples(sample, sensor.get(e.getKey()), e.getValue());
    }
  }

  @VisibleForTesting
  void addSampleToTuples(Sample sample, TreeMap<SampleTuple, String> tuples, Serializable value) {
    while(maximumEntriesPerSensor <= tuples.size()) {
      tuples.pollFirstEntry();
    }
    tuples.put(from(sample, value), null);
  }

  private SampleTuple from(Sample sample, Serializable serializable) {
    return from(sample.getId().toString(), sample.getTimestamp(), serializable);
  }

  private SampleTuple from(String id, DateTime timestamp, Object value) {
    return new SampleTuple(id, timestamp, value);
  }

  @VisibleForTesting
  class SampleTuple implements Comparable<SampleTuple> {
    public DateTime timestamp;
    public Object value;
    public String id;

    SampleTuple(String id, DateTime timestamp, Object value) {
      this.id = id;
      this.timestamp = timestamp;
      this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof SampleTuple) {
        return id.equals(((SampleTuple) obj).id);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }

    @Override
    public int compareTo(SampleTuple o) {
      return timestamp.compareTo(o.timestamp);
    }
  }

  public synchronized String toJSON() {
    JSONObject platformJson = new JSONObject();

    for (Map.Entry<String, Map<String, Map<String, TreeMap<SampleTuple, String>>>> platform : data.entrySet()) {
      JSONObject sensorJson = new JSONObject();

      for (Map.Entry<String, Map<String, TreeMap<SampleTuple, String>>> sensor : platform.getValue().entrySet()) {
        JSONObject valuesJson = new JSONObject();

        for (Map.Entry<String, TreeMap<SampleTuple, String>> values : sensor.getValue().entrySet()) {
          JSONArray valueJson = new JSONArray();

          for (SampleTuple sampleTuple : values.getValue().keySet()) {
            valueJson.add(entry(sampleTuple));
          }

          valuesJson.put(values.getKey(), valueJson);
        }

        sensorJson.put(sensor.getKey(), valuesJson);
      }

      platformJson.put(platform.getKey(), sensorJson);
    }

    return platformJson.toJSONString();
  }

  private JSONObject entry(SampleTuple sampleTuple) {
    JSONObject entry = new JSONObject();
    entry.put("timestamp", formatForJson(sampleTuple.timestamp));
    entry.put("value", sampleTuple.value);
    return entry;
  }

  private String formatForJson(DateTime timestamp) {
    return SampleDateFormatter.formatForJson(timestamp);
  }

}
