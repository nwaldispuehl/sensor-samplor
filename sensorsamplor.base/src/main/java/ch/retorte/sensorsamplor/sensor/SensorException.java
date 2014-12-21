package ch.retorte.sensorsamplor.sensor;

import org.joda.time.DateTime;

import static org.joda.time.DateTime.now;

public class SensorException extends Exception implements Sample {

  private DateTime date = now();
  private String platformIdentifier;
  private String sensorType;

  public SensorException(String platformIdentifier, String sensorType, String message) {
    super(message);
    this.platformIdentifier = platformIdentifier;
    this.sensorType = sensorType;
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
}
