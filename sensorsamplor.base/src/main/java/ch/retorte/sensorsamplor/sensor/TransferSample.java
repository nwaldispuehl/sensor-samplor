package ch.retorte.sensorsamplor.sensor;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static ch.retorte.sensorsamplor.utils.SampleDateFormatter.format;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.UUID.randomUUID;
import static org.joda.time.DateTime.now;

/**
 * Sample implementation which is sent over the bus.
 */
public class TransferSample implements Sample {

  private static final String FIELD_DELIMITER = " -- ";

  private final UUID uuid = randomUUID();
  private final DateTime timestamp;
  private final String platformIdentifier;
  private final String sensorType;
  private final Map<String, Serializable> data = newHashMap();

  public TransferSample(String platformIdentifier, String sensorType) {
    this(now(), platformIdentifier, sensorType);
  }

  public TransferSample(DateTime timestamp, String platformIdentifier, String sensorType) {
    this.timestamp = timestamp;
    this.platformIdentifier = platformIdentifier;
    this.sensorType = sensorType;
  }

  public TransferSample addItem(String key, Serializable value) {
    data.put(key, value);
    return this;
  }

  @Override
  public String toString() {
    return formatSample(this) + formatData(getData());
  }

  public static String formatSample(Sample sample) {
    return format(sample.getTimestamp()) + FIELD_DELIMITER + sample.getPlatformIdentifier() + FIELD_DELIMITER + sample.getSensorType() + FIELD_DELIMITER +  sample.getId() + FIELD_DELIMITER;
  }

  public static String formatData(Map<String, Serializable> data) {
    JSONObject jsonObject = new JSONObject();
    for (Map.Entry<String, Serializable> e : data.entrySet()) {
      jsonObject.put(e.getKey(), e.getValue());
    }
    return jsonObject.toJSONString();
  }

  @Override
  public UUID getId() {
    return uuid;
  }

  @Override
  public DateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public String getPlatformIdentifier() {
    return platformIdentifier;
  }

  @Override
  public String getSensorType() {
    return sensorType;
  }

  @Override
  public Map<String, Serializable> getData() {
    return data;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TransferSample) {
      return uuid.equals(((TransferSample) obj).uuid);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public int compareTo(Sample o) {
    return timestamp.compareTo(o.getTimestamp());
  }
}
