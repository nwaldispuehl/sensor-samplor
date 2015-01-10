package ch.retorte.sensorsamplor.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Formats the date in a nice way.
 */
public class SampleDateFormatter {

  private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd:HH:mm:ss Z");

  private static final DateTimeFormatter jsonDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  public static String format(DateTime dateTime) {
    return dateFormatter.print(dateTime);
  }

  public static String formatForJson(DateTime dateTime) {
    return jsonDateFormatter.print(dateTime);
  }

}
