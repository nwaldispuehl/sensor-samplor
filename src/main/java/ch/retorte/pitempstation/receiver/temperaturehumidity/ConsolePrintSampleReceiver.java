package ch.retorte.pitempstation.receiver.temperaturehumidity;

import ch.retorte.pitempstation.receiver.SampleReceiver;
import ch.retorte.pitempstation.sensor.Sample;
import ch.retorte.pitempstation.sensor.temperaturehumidity.ErrorSample;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySample;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.String.valueOf;

/**
 * Prints all received samples instantly to the standard out.
 */
public class ConsolePrintSampleReceiver implements SampleReceiver {

  public void processSample(Sample sample) {
    if (sample instanceof TemperatureHumiditySample) {
      TemperatureHumiditySample tempHumSample = (TemperatureHumiditySample) sample;
      System.out.println(sample.getDate() + " " + formatToDecimal(tempHumSample.getTemperature()) + "°C, " + formatToDecimal(tempHumSample.getHumidity()) + "%");
    }
  }

  private String formatToDecimal(double value) {
      return valueOf(round(value));
  }

  private double round(double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }

  public void processError(Sample sample) {
    if (sample instanceof ErrorSample) {
      System.err.println(sample.getDate() + " " + ((ErrorSample) sample).getErrorMessage());
    }
  }

}
