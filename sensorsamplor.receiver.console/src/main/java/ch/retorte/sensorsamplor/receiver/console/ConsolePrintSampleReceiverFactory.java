package ch.retorte.sensorsamplor.receiver.console;

import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;

import static ch.retorte.sensorsamplor.receiver.console.ConsolePrintSampleReceiver.IDENTIFIER;

/**
 * Created by nw on 19.12.14.
 */
public class ConsolePrintSampleReceiverFactory implements ReceiverFactory {
  @Override
  public SampleReceiver createReceiverFor(String platformIdentifier, SensorBus sensorBus) {
    return new ConsolePrintSampleReceiver(sensorBus);
  }

  @Override
  public String getIdentifier() {
    return IDENTIFIER;
  }
}
