package ch.retorte.sensorsamplor.receiver.exporter;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.utils.SampleDateFormatter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newConcurrentMap;

/**
 * Holds a series of samples.
 */
public class SampleCollection {

  private int maximumEntriesPerSensor;

  /* Map of nodes, of sensors, of keys, list of value tuples.  */
  private Map<String, Map<String, Map<String, List<SampleTuple>>>> data = newConcurrentMap();

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

  private Map<String, Map<String, List<SampleTuple>>> getNode(Sample sample) {
    return getNode(sample.getPlatformIdentifier());
  }

  private Map<String, Map<String, List<SampleTuple>>> getNode(String node) {
    return data.get(node);
  }

  private void createNodeFor(Sample sample) {
    data.put(sample.getPlatformIdentifier(), Maps.<String, Map<String, List<SampleTuple>>>newConcurrentMap());
  }

  private boolean hasSensor(Sample sample) {
    return hasSensor(sample.getPlatformIdentifier(), sample.getSensorType());
  }

  private boolean hasSensor(String node, String sensor) {
    return getSensor(node, sensor) != null;
  }

  private Map<String, List<SampleTuple>> getSensorFor(Sample sample) {
    return getSensor(sample.getPlatformIdentifier(), sample.getSensorType());
  }

  private Map<String, List<SampleTuple>> getSensor(String node, String sensor) {
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
    getNode(sample).put(sample.getSensorType(), Maps.<String, List<SampleTuple>>newConcurrentMap());
  }

  private void createValuesFor(Sample sample) {
    createValuesFor(sample.getPlatformIdentifier(), sample.getSensorType(), sample.getData().keySet());
  }

  private void createValuesFor(String node, String sensor, Collection<String> values) {
    Map<String, List<SampleTuple>> valuesMap = getSensor(node, sensor);
    for (String v : values) {
      valuesMap.put(v, Lists.<SampleTuple>newArrayList());
    }
  }

  private void addDataOf(Sample sample) {
    Map<String, List<SampleTuple>> sensor = getSensorFor(sample);
    for (Map.Entry<String, Serializable> e : sample.getData().entrySet()) {
      sensor.get(e.getKey()).add(new SampleTuple(sample.getTimestamp(), e.getValue()));
    }
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

  public synchronized String toJSON() {
    JSONObject platformJson = new JSONObject();

    for (Map.Entry<String, Map<String, Map<String, List<SampleTuple>>>> platform : data.entrySet()) {
      JSONObject sensorJson = new JSONObject();

      for (Map.Entry<String, Map<String, List<SampleTuple>>> sensor : platform.getValue().entrySet()) {
        JSONObject valuesJson = new JSONObject();

        for (Map.Entry<String, List<SampleTuple>> values : sensor.getValue().entrySet()) {
          JSONArray valueJson = new JSONArray();

          for (SampleTuple sampleTuple : values.getValue()) {
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
