package ch.retorte.sensorsamplor.receiver.console;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.SensorException;

/**
 * Prints all received samples instantly to the standard out.
 */
public class ConsolePrintSampleReceiver implements SampleReceiver {

  public static final String IDENTIFIER = "console";

  public void processSample(Sample sample) {
      System.out.println(sample);
  }

  public void processError(SensorException sensorException) {
      System.err.println(format(sensorException));
  }

  private String format(SensorException sensorException) {
    return sensorException.getDate() + " " +
            sensorException.getPlatformIdentifier() + " " +
            sensorException.getSensorType() + " " +
            sensorException.getMessage();
  }
}
