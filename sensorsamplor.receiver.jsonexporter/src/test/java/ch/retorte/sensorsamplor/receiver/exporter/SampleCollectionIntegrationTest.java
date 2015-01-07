package ch.retorte.sensorsamplor.receiver.exporter;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.TransferSample;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests end to end functionality of the SampleCollection.
 */
public class SampleCollectionIntegrationTest {

  private SampleCollection sut;
  private DateTime d1 = DateTime.parse("2015-01-10T10:00:00");
  private DateTime d2 = DateTime.parse("2015-01-10T10:01:00");

  @Before
  public void setup() {
    sut = new SampleCollection(10);
  }

  @Test
  public void shouldProvideEmptyJsonOnEmptyInput() {
    // when
    String json = sut.toJSON();

    // then
    assertThat(json, is("{}"));
  }

  @Test
  public void shouldProvideJsonForSingleSample() {
    // given
    Sample s = sampleFrom(d1, "myPlatform", "mySensor", "myKey", "myValue");

    // when
    sut.addSample(s);
    String json = sut.toJSON();

    // then
    assertThat(json, is("{\"myPlatform\":{\"mySensor\":{\"myKey\":[{\"2015-01-10T10:00:00.000+01:00\":\"myValue\"}]}}}"));
  }

  @Test
  public void shouldProvideJsonForMultipleSample() {
    // given
    Sample s1 = sampleFrom(d1, "myPlatform", "mySensor", "myKey", "myValue1");
    Sample s2 = sampleFrom(d2, "myPlatform", "mySensor", "myKey", "myValue2");

    // when
    sut.addSample(s1);
    sut.addSample(s2);
    String json = sut.toJSON();

    // then
    assertThat(json, is("{\"myPlatform\":{\"mySensor\":{\"myKey\":[{\"2015-01-10T10:00:00.000+01:00\":\"myValue1\"},{\"2015-01-10T10:01:00.000+01:00\":\"myValue2\"}]}}}"));
  }

  private Sample sampleFrom(DateTime timestamp, String platformIdentifier, String sensorType, Object... keyAndValue) {
    TransferSample sample = new TransferSample(timestamp, platformIdentifier, sensorType);
    for (int i = 0; i < (keyAndValue.length / 2); i++) {
      sample.addItem((String) keyAndValue[i], (java.io.Serializable) keyAndValue[i+1]);
    }

    return sample;
  }
}
