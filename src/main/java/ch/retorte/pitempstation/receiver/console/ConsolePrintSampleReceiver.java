package ch.retorte.pitempstation.receiver.console;

import ch.retorte.pitempstation.receiver.SampleReceiver;
import ch.retorte.pitempstation.sensor.Sample;

/**
 * Prints all received samples instantly to the standard out.
 */
public class ConsolePrintSampleReceiver implements SampleReceiver {

  public void processSample(Sample sample) {
      System.out.println(sample);
  }

  public void processError(Sample sample) {
      System.err.println(sample);
  }

}
