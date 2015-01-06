package ch.retorte.sensorsamplor.receiver.exporter;

import ch.retorte.sensorsamplor.sensor.Sample;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

  }


  private void addDataOf(Sample sample) {

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

  public String toJSON() {

//    JSONObject jsonObject = new JSONObject();
//    for (Map.Entry<String, Serializable> e : data.entrySet()) {
//      jsonObject.put(e.getKey(), e.getValue());
//    }
//    String output = jsonObject.toJSONString();

    return null;
  }


}