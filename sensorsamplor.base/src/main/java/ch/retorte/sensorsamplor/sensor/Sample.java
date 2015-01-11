package ch.retorte.sensorsamplor.sensor;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * A single data item.
 */
public interface Sample extends Serializable, Comparable<Sample> {

  /**
   * Provides a unique id for this sample.
   */
  UUID getId();

  /**
   * Returns the point in time this sample was taken.
   */
  DateTime getTimestamp();

  /**
   * Returns the string identifier of the platform where the sample was taken.
   * This might be the name of the computer or some geographic designation which
   * helps to tell the different samples apart, resp. to assign it doubtless to
   * a single sensor platform.
   */
  String getPlatformIdentifier();

  /**
   * Returns an identifier denoting the kind/make of sensor, e.g. 'temperature', or 'eatenDonuts'.
   */
  String getSensorType();

  /**
   * This returns the data of the sensor sample in a key value fashion.
   */
  Map<String, Serializable> getData();
}
