package ch.retorte.sensorsamplor.sensor;

import org.joda.time.DateTime;

/**
 * The most elementary data item a sample can show - the time stamp.
 */
public interface Sample {

  DateTime getDate();
}
