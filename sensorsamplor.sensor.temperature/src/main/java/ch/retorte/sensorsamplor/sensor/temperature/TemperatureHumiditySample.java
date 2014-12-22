package ch.retorte.sensorsamplor.sensor.temperature;

import ch.retorte.sensorsamplor.sensor.FormattedSample;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static ch.retorte.sensorsamplor.sensor.temperature.am2302.Am2302Sensor.IDENTIFIER;
import static java.util.UUID.randomUUID;
import static org.joda.time.DateTime.now;

/**
 * Immutable sensor sample for temperature/humidity sensor.
 */
public class TemperatureHumiditySample extends FormattedSample {

  private final UUID uuid = randomUUID();
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
  public UUID getId() {
    return uuid;
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
  public String getFormattedPayload() {
    return toOneDigitDouble(getTemperature()) + "°C, " + toOneDigitDouble(getHumidity()) + "%";
  }

  private double toOneDigitDouble(Double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }

}
