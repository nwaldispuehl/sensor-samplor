package ch.retorte.sensorsamplor.receiver;

import ch.retorte.sensorsamplor.bus.SensorBus;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the SampleReceiverManager.
 */
public class SampleReceiverManagerTest {

  SensorBus sensorBus = mock(SensorBus.class);
  List<SampleReceiver> receivers = mock(ArrayList.class);
  SampleReceiverManager sut = new SampleReceiverManager(sensorBus, receivers, "", "");


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
