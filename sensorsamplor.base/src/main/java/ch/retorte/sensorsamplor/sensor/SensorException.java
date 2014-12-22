package ch.retorte.sensorsamplor.sensor;

import org.joda.time.DateTime;

import java.util.UUID;

import static ch.retorte.sensorsamplor.sensor.FormattedSample.formatSample;
import static java.util.UUID.randomUUID;
import static org.joda.time.DateTime.now;

public class SensorException extends Exception implements Sample {

  private UUID uuid = randomUUID();
  private DateTime date = now();
  private String platformIdentifier;
  private String sensorType;

  public SensorException(String platformIdentifier, String sensorType, String message) {
    super(message);
    this.platformIdentifier = platformIdentifier;
    this.sensorType = sensorType;
  }

  @Override
  public UUID getId() {
    return uuid;
  }

  public DateTime getDate() {
    return date;
  }

  public String getPlatformIdentifier() {
    return platformIdentifier;
  }

  public String getSensorType() {
    return sensorType;
  }

  @Override
  public String toString() {
    return formatSample(this) + getMessage();
  }
}
