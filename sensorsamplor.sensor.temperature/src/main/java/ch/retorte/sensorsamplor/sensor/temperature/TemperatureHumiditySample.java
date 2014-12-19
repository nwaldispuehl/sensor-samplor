package ch.retorte.sensorsamplor.sensor.temperature;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.utils.SampleDateFormatter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ch.retorte.sensorsamplor.sensor.temperature.am2302.Am2302Sensor.IDENTIFIER;
import static ch.retorte.sensorsamplor.utils.SampleDateFormatter.format;
import static org.joda.time.DateTime.now;


/**
 * Immutable sensor sample for temperature/humidity sensor.
 */
public class TemperatureHumiditySample implements Sample {



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

  @Override
  public String getSensorType() {
    return IDENTIFIER;
  }

  public Double getTemperature() {
    return temperature;
  }

  public Double getHumidity() {
    return humidity;
  }

  @Override
  public String toString() {
    return "[" + format(getDate()) + " " + getPlatformIdentifier() + "] " + toOneDigitDouble(getTemperature()) + "°C, " + toOneDigitDouble(getHumidity()) + "%";
  }



  private double toOneDigitDouble(Double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }

}
