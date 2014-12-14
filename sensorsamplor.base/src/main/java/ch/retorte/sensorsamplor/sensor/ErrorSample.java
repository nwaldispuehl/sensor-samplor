package ch.retorte.sensorsamplor.sensor;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static org.joda.time.DateTime.now;

/**
 * Sample to be produced if errors occurs when measuring.
 */
public class ErrorSample implements Sample {

  private final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd:HH:mm:ss Z");

  private final DateTime date = now();
  private final String platformIdentifier;
  private final String errorMessage;

  public ErrorSample(String platformIdentifier, String errorMessage) {
    this.platformIdentifier = platformIdentifier;
    this.errorMessage = errorMessage;
  }

  @Override
  public String getPlatformIdentifier() {
    return platformIdentifier;
  }

  @Override
  public DateTime getDate() {
    return date;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public String toString() {
    return "[" + getFormattedDate() + " " + getPlatformIdentifier() + "] " + getErrorMessage();
  }

  private String getFormattedDate() {
    return dateFormatter.print(getDate());
  }
}
