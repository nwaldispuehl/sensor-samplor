package ch.retorte.sensorsamplor.sensor;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the SamplePatternChecker.
 */
public class SamplePatternCheckerTest {

  SamplePatternChecker sut = new SamplePatternChecker();

  @Test
  public void shouldMatchPattern() {
    assertTrue(matchesPattern(".*", "temperature"));
    assertTrue(matchesPattern(".*", "console"));
    assertTrue(matchesPattern("console|logfile", "logfile"));
    assertTrue(matchesPattern("myRaspberryPi[0-9]{2}", "myRaspberryPi38"));

    assertFalse(matchesPattern("console", "logfile"));
    assertFalse(matchesPattern("myRaspberryPi[0-9]{2}", "myRaspberryPi383"));
  }

  private boolean matchesPattern(String pattern, String toMatch) {
    return sut.patternMatches(Pattern.compile(pattern), toMatch);
  }
}
