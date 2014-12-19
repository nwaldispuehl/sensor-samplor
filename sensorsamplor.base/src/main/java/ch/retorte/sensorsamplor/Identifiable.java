package ch.retorte.sensorsamplor;

/**
 *
 */
public interface Identifiable {

  /**
   * Produces a unique identifier which is supposed to identify this implementation for configuration.
   */
  String getIdentifier();
}
