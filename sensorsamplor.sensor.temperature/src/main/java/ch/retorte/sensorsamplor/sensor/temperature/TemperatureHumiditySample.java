package ch.retorte.sensorsamplor.sensor.temperature;

import ch.retorte.sensorsamplor.sensor.Sample;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.joda.time.DateTime.now;


/**
 * Immutable sensor sample for temperature/humidity sensor.
 */
public class TemperatureHumiditySample implements Sample {

  private final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd:HH:mm:ss Z");

  private final DateTime date = now();
  private final String platformIdentifier;
  private final Double humidity;
  private final Double temperature;

  public TemperatureHumiditySample(String platformIdentifier, Double temperature, Double humidity) {
    this.platformIdentifier = platformIdentifier;
    this.temperature = temperature;
    this.humidity = humidity;
  }

  @Override
  public DateTime getDate() {
    return date;
  }

  @Override
  public String getPlatformIdentifier() {
    return platformIdentifier;
  }

  public Double getTemperature() {
    return temperature;
  }

  public Double getHumidity() {
    return humidity;
  }

  @Override
  public String toString() {
    return "[" + getFormattedDate() + " " + getPlatformIdentifier() + "] " + toOneDigitDouble(getTemperature()) + "°C, " + toOneDigitDouble(getHumidity()) + "%";
  }

  private String getFormattedDate() {
    return dateFormatter.print(getDate());
  }

  private double toOneDigitDouble(Double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }

}
