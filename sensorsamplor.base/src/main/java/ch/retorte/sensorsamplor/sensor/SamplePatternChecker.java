package ch.retorte.sensorsamplor.sensor;

import com.google.common.annotations.VisibleForTesting;

import java.util.regex.Pattern;

/**
 * Determines if a Sample is valid according to the provided patterns.
 */
public class SamplePatternChecker {

  private Pattern sensorPattern;
  private Pattern platformPattern;

  @VisibleForTesting
  SamplePatternChecker() {}

  public SamplePatternChecker(String sensorPatternString, String platformPatternString) {
    compilePattern(sensorPatternString, platformPatternString);
  }

  private void compilePattern(String sensorPatternString, String platformPatternString) {
    sensorPattern = Pattern.compile(sensorPatternString);
    platformPattern = Pattern.compile(platformPatternString);
  }

  public boolean matches(Sample sample) {
    return sensorTypeMatches(sample) && platformIdentifierMatches(sample);
  }

  private boolean sensorTypeMatches(Sample sample) {
    return patternMatches(sensorPattern, sample.getSensorType());
  }

  private boolean platformIdentifierMatches(Sample sample) {
    return patternMatches(platformPattern, sample.getPlatformIdentifier());
  }

  @VisibleForTesting
  boolean patternMatches(Pattern pattern, String stringToMatch) {
    return pattern.matcher(stringToMatch).matches();
  }
}
