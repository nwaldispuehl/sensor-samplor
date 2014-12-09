package ch.retorte.pitempstation.receiver.temperaturehumidity;

import ch.retorte.pitempstation.receiver.SampleReceiver;
import ch.retorte.pitempstation.sensor.Sample;
import ch.retorte.pitempstation.sensor.temperaturehumidity.ErrorSample;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySample;

/**
 * Prints all received samples instantly to the standard out.
 */
public class ConsolePrintSampleReceiver implements SampleReceiver {

  public void processSample(Sample sample) {
    if (sample instanceof TemperatureHumiditySample) {
      TemperatureHumiditySample tempHumSample = (TemperatureHumiditySample) sample;
      System.out.println(sample.getDate() + " " + tempHumSample.getTemperature() + "° " + tempHumSample.getHumidity() + "%");
    }
  }

  public void processError(Sample sample) {
    if (sample instanceof ErrorSample) {
      System.err.println(sample.getDate() + " " + ((ErrorSample) sample).getErrorMessage());
    }
    else {
      System.err.println(sample.getDate() + " No sensor reading possible: ");
    }
  }

}
