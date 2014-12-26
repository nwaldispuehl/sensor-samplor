package ch.retorte.sensorsamplor.sensor;

/**
 * Class denoting a sensor error. We are not using an exception because we are sending this over the bus.
 */
public class ErrorSample extends TransferSample {

  public ErrorSample(String platformIdentifier, String sensorType, String message) {
    super(platformIdentifier, sensorType);
    addItem("message", message);
  }
}
