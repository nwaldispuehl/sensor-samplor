package ch.retorte.pitempstation.sensor.temperaturehumidity;

import ch.retorte.pitempstation.sensor.Sample;
import org.joda.time.DateTime;

/**
 * Created by nw on 07.12.14.
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
}
