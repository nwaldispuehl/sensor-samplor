package ch.retorte.sensorsamplor.sensor.temperaturehumidity;

import ch.retorte.sensorsamplor.sensor.Sample;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.String.valueOf;

/**
 * Immutable sensor sample for temperature/humidity sensor.
 */
public class TemperatureHumiditySample implements Sample {

  private final DateTime date;
  private final Double humidity;
  private final Double temperature;

  public TemperatureHumiditySample(Double temperature, Double humidity) {
    this.date = DateTime.now();
    this.temperature = temperature;
    this.humidity = humidity;
  }

  public DateTime getDate() {
    return date;
  }

  public Double getTemperature() {
    return temperature;
  }

  public Double getHumidity() {
    return humidity;
  }

  @Override
  public String toString() {
    return getDate() + " " + toOneDigitDouble(getTemperature()) + "°C, " + toOneDigitDouble(getHumidity()) + "%";
  }

  private double toOneDigitDouble(Double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }

}
