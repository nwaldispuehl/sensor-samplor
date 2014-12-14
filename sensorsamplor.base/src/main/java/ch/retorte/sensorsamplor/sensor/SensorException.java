package ch.retorte.sensorsamplor.sensor;

public class SensorException extends Exception {

  private String platformIdentifier;

  public SensorException(String platformIdentifier, String message) {
    super(message);
    this.platformIdentifier = platformIdentifier;
  }

  public String getPlatformIdentifier() {
    return platformIdentifier;
  }
}
