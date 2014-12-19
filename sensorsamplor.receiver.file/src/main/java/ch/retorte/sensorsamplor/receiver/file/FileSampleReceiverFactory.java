package ch.retorte.sensorsamplor.receiver.file;

import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;

import static ch.retorte.sensorsamplor.receiver.file.FileSampleReceiver.IDENTIFIER;

/**
 * Created by nw on 19.12.14.
 */
public class FileSampleReceiverFactory implements ReceiverFactory {

  private String logFilePath;

  public FileSampleReceiverFactory(String logFilePath) {
    this.logFilePath = logFilePath;
  }

  @Override
  public SampleReceiver createReceiverFor(String platformIdentifier, SensorBus sensorBus) {
    return new FileSampleReceiver(sensorBus, logFilePath);
  }

  @Override
  public String getIdentifier() {
    return IDENTIFIER;
  }
}
