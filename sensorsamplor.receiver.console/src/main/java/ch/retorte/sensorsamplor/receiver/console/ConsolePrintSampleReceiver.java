package ch.retorte.sensorsamplor.receiver.console;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.SensorException;

import java.util.List;

/**
 * Prints all received samples instantly to the standard out.
 */
public class ConsolePrintSampleReceiver implements SampleReceiver {

  public static final String IDENTIFIER = "console";

  public void processSample(List<Sample> sampleBuffer, Sample sample) {
      System.out.println(sample);
  }

  public void processError(List<Sample> sampleBuffer, SensorException sensorException) {
      System.err.println(sensorException);
  }
}
