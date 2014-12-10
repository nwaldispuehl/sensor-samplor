package ch.retorte.pitempstation.sensor.temperaturehumidity;

import ch.retorte.pitempstation.sensor.Sample;
import org.joda.time.DateTime;

/**
 * Sample to be produced if errors occurs when measuring.
 */
public class ErrorSample implements Sample {

  private final DateTime date;
  private final String errorMessage;

  public ErrorSample(String errorMessage) {
    this.date = DateTime.now();
    this.errorMessage = errorMessage;
  }

  @Override
  public DateTime getDate() {
    return date;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
