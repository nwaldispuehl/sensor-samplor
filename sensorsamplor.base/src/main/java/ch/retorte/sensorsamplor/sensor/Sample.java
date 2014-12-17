package ch.retorte.sensorsamplor.sensor;

import org.joda.time.DateTime;

/**
 * The most elementary data item a sample can show - the time stamp.
 */
public interface Sample {

  /**
   * Returns the point in time this sample was taken.
   */
  DateTime getDate();

  /**
   * Returns the string identifier of the platform where the sample was taken. This might be the name of the computer or some geographic designation.
   */
  String getPlatformIdentifier();

  /**
   * Returns an identifier denoting the kind/make of sensor, e.g. 'temperature', or 'eatenDonuts'.
   */
  String getSensorType();
}
