package ch.retorte.sensorsamplor.receiver.console;

import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;

import static ch.retorte.sensorsamplor.receiver.console.ConsolePrintSampleReceiver.IDENTIFIER;

/**
 * Factory for the console print receiver.
 */
public class ConsolePrintSampleReceiverFactory implements ReceiverFactory {

  @Override
  public SampleReceiver createReceiver() {
    return new ConsolePrintSampleReceiver();
  }

  @Override
  public String getIdentifier() {
    return IDENTIFIER;
  }
}
