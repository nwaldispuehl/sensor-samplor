package ch.retorte.sensorsamplor.receiver.exporter;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the JSON exporter receiver.
 */
public class JsonExporterSampleReceiverTest {

  JsonExporterSampleReceiver sut = new JsonExporterSampleReceiver("", 1, "", "");


  @Test
  public void shouldAtLeastSecondsPassedBetween() {
    // given
    DateTime t0 = d("2015-01-06T08:00:00");
    DateTime t1 = d("2015-01-06T08:00:05");

    // when/then
    assertTrue(sut.areAtLeastSecondsPassedBetween(t0, t1, 4));
    assertTrue(sut.areAtLeastSecondsPassedBetween(t0, t1, 5));
    assertFalse(sut.areAtLeastSecondsPassedBetween(t0, t1, 6));
  }

  private DateTime d(String d) {
    return DateTime.parse(d);
  }

  @Test
  public void shouldCompareDates() {
    // given
    DateTime t0 = d("2015-01-13T07:49:32.062+01:00");
    DateTime t1 = d("2015-01-13T07:49:36.045+01:00");

    // when/then
    assertTrue(sut.areAtLeastSecondsPassedBetween(t0, t1, 3));
    assertFalse(sut.areAtLeastSecondsPassedBetween(t0, t1, 10));
  }
}

