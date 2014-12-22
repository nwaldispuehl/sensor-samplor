package ch.retorte.sensorsamplor.receiver;

import ch.retorte.sensorsamplor.Configurable;
import ch.retorte.sensorsamplor.Identifiable;

/**
 * Creates an implementation of the sample receiver.
 */
public interface ReceiverFactory extends Configurable, Identifiable {

  /**
   * Returns the receiver of this implementation.
   */
  SampleReceiver createReceiver();
}
