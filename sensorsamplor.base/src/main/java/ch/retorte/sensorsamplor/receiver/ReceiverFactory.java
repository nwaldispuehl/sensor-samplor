package ch.retorte.sensorsamplor.receiver;

import ch.retorte.sensorsamplor.Identifiable;
import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.sensor.Sensor;

/**
 * Creates an implementation of the sample receiver.
 */
public interface ReceiverFactory extends Identifiable {

  /**
   * Returns the receiver of this implementation.
   */
  SampleReceiver createReceiverFor(String platformIdentifier, SensorBus sensorBus);
}
