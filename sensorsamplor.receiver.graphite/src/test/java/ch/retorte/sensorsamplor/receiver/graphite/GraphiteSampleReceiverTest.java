package ch.retorte.sensorsamplor.receiver.graphite;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.TransferSample;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Unit tests for the graphite sample receiver.
 */
public class GraphiteSampleReceiverTest {

  private GraphiteSampleReceiver sut = new GraphiteSampleReceiver("", 0, "", "");
  private Sample sample;

  @Before
  public void setup() {
    sample = sampleOf(2015, 04, 25, "myPlatform", "mySensor");
  }

  @Test
  public void shouldProducePathOfSample() {
    assertProduces(sut.pathOf(sample, "myKey1"), "myPlatform.mySensor.myKey1");
    assertProduces(sut.pathOf(sample, "myKey2"), "myPlatform.mySensor.myKey2");
  }

  @Test
  public void shouldProduceValueOfSample() {
    assertProduces(sut.valueOf(sample, "myKey1"), "123");
    assertProduces(sut.valueOf(sample, "myKey2"), "abc");
  }

  @Test
  public void shouldProduceTimestampOfSample() {
    assertProduces(sut.timestampOf(sample), "1429920000");
  }

  @Test
  public void shouldProduceGraphiteFormatOfSample() {
    assertProduces(sut.graphiteFormatOf(sample, "myKey1"), "myPlatform.mySensor.myKey1 123 1429920000");
    assertProduces(sut.graphiteFormatOf(sample, "myKey2"), "myPlatform.mySensor.myKey2 abc 1429920000");
  }

  private void assertProduces(String input, String expectedOutput) {
    assertThat(input, is(expectedOutput));
  }

  private Sample sampleOf(int year, int month, int day, String platformIdentifier, String sensorType) {
    TransferSample sample = spy(new TransferSample(new DateTime(year, month, day, 0, 0, DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT"))), platformIdentifier, sensorType));
    sample.addItem("myKey1", 123);
    sample.addItem("myKey2", "abc");
    return sample;
  }
}
