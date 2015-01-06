package ch.retorte.sensorsamplor.receiver.exporter;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Unit tests for the JSON exporter receiver.
 */
public class JsonExporterSampleReceiverTest {

  JsonExporterSampleReceiver sut = new JsonExporterSampleReceiver("", 1);


  @Test
  public void shouldAtLeastSecondsPassedBetween() {
    // given
    DateTime t0 = d("2015-01-06T08:00:00");
    DateTime t1 = d("2015-01-06T08:00:05");

    // when/then
    assertFalse(sut.areAtLeastSecondsPassedBetween(t0, t1, 4));
    assertFalse(sut.areAtLeastSecondsPassedBetween(t0, t1, 5));
    assertTrue(sut.areAtLeastSecondsPassedBetween(t0, t1, 6));
  }

  private DateTime d(String d) {
    return DateTime.parse(d);
  }
}
