package ch.retorte.sensorsamplor.sensor;

import static ch.retorte.sensorsamplor.utils.SampleDateFormatter.format;

/**
 * Provides some formatting means.
 */
public abstract class FormattedSample implements Sample {

  private static final String FIELD_DELIMITER = " -- ";

  @Override
  public String toString() {
    return formatSample(this) + getFormattedPayload();
  }

  public static String formatSample(Sample sample) {
    return format(sample.getDate()) + FIELD_DELIMITER + sample.getPlatformIdentifier() + FIELD_DELIMITER + sample.getSensorType() + FIELD_DELIMITER +  sample.getId() + FIELD_DELIMITER;
  }

  public abstract String getFormattedPayload();
}
