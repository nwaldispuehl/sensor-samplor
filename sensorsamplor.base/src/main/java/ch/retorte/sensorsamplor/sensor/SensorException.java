package ch.retorte.sensorsamplor.sensor;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static ch.retorte.sensorsamplor.sensor.TransferSample.formatData;
import static ch.retorte.sensorsamplor.sensor.TransferSample.formatSample;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.UUID.randomUUID;
import static org.joda.time.DateTime.now;

public class SensorException extends Exception implements Sample {

  private final UUID uuid = randomUUID();
  private final DateTime date = now();
  private final String platformIdentifier;
  private final String sensorType;
  private final Map<String, Serializable> data = newHashMap();

  public SensorException(String platformIdentifier, String sensorType, String message) {
    super(message);
    this.platformIdentifier = platformIdentifier;
    this.sensorType = sensorType;
    data.put("message", message);
  }

  @Override
  public UUID getId() {
    return uuid;
  }

  public DateTime getTimestamp() {
    return date;
  }

  public String getPlatformIdentifier() {
    return platformIdentifier;
  }

  public String getSensorType() {
    return sensorType;
  }

  @Override
  public Map<String, Serializable> getData() {
    return data;
  }

  @Override
  public String toString() {
    return formatSample(this) + formatData(getData());
  }
}
