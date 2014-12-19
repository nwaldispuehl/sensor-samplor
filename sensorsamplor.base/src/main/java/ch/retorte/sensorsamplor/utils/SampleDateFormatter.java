package ch.retorte.sensorsamplor.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by nw on 19.12.14.
 */
public class SampleDateFormatter {

  private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd:HH:mm:ss Z");

  public static String format(DateTime dateTime) {
    return dateFormatter.print(dateTime);
  }

}
