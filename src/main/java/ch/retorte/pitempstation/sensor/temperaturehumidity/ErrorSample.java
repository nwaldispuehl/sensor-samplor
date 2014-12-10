package ch.retorte.pitempstation.sensor.temperaturehumidity;

import ch.retorte.pitempstation.sensor.Sample;
import org.joda.time.DateTime;

/**
 * Created by nw on 09.12.14.
 */
public class ErrorSample implements Sample {

  private DateTime date;
  private String errorMessage;

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
