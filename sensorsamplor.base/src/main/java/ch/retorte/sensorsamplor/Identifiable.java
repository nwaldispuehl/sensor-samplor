package ch.retorte.sensorsamplor;

/**
 * Denotes identifiable classes.
 */
public interface Identifiable {

  /**
   * Produces a unique identifier which is supposed to identify this implementation for configuration.
   */
  String getIdentifier();
}
