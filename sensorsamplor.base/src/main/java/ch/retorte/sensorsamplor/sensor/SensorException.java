package ch.retorte.sensorsamplor.sensor;

public class SensorException extends Exception {

  private final String platformIdentifier;
  private final String sensorType;

  public SensorException(String platformIdentifier, String sensorType, String message) {
    super(message);
    this.platformIdentifier = platformIdentifier;
    this.sensorType = sensorType;
  }

  public String getPlatformIdentifier() {
    return platformIdentifier;
  }

  public String getSensorType() {
    return sensorType;
  }

}
