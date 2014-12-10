package ch.retorte.pitempstation.sensor.temperaturehumidity.am2302;

/**
 * Sensor error codes taken from the respective header file 'common_dht_read.h'.
 */
public enum Am2302SensorStatusCode {


  DHT_ERROR_TIMEOUT(-1, "Sensor timeout."),
  DHT_ERROR_CHECKSUM(-2, "Checksum error."),
  DHT_ERROR_ARGUMENT(-3, "Wrong arguments provided."),
  DHT_ERROR_GPIO(-4, "Sensor not properly wired."),
  DHT_SUCCESS(0, null),
  UNKNOWN(-99, "Unknown problem.");

  private int statusCode;
  private String errorMessage;

  Am2302SensorStatusCode(int errorCode, String errorMessage) {
    this.statusCode = errorCode;
    this.errorMessage = errorMessage;
  }

  public int code() {
    return statusCode;
  }

  public String message() {
    return errorMessage;
  }

  public static Am2302SensorStatusCode valueOfStatus(int status) {
    for (Am2302SensorStatusCode c : values()) {
      if (c.code() == status) {
        return c;
      }
    }
    return null;
  }
}
