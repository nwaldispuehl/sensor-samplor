package ch.retorte.sensorsamplor.receiver.exporter;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.TransferSample;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests end to end functionality of the SampleCollection.
 */
public class SampleCollectionIntegrationTest {

  private static final String PLATFORM = "myPlatform";

  private SampleCollection sut;
  private DateTime d1 = DateTime.parse("2015-01-10T10:00:00");
  private DateTime d2 = DateTime.parse("2015-01-10T10:01:00");
  private DateTime d3 = DateTime.parse("2015-01-10T10:02:00");
  private DateTime d4 = DateTime.parse("2015-01-10T10:03:00");
  private DateTime d5 = DateTime.parse("2015-01-10T10:04:00");

  @Before
  public void setup() {
    sut = new SampleCollection(3);
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
    Sample s = sampleFrom(d1, PLATFORM, "mySensor", "myKey", "myValue");

    // when
    sut.addSample(s);
    String json = sut.toJSON();

    // then
    assertThat(json, is("{\"myPlatform\":{\"mySensor\":{\"myKey\":[{\"timestamp\":\"2015-01-10T10:00:00.000+0100\",\"value\":\"myValue\"}]}}}"));
  }

  @Test
  public void shouldProvideJsonForMultipleSamples() {
    // given
    Sample s1 = sampleFrom(d1, PLATFORM, "mySensor", "myKey", "myValue1");
    Sample s2 = sampleFrom(d2, PLATFORM, "mySensor", "myKey", "myValue2");

    // when
    sut.addSample(s1);
    sut.addSample(s2);
    String json = sut.toJSON();

    // then
    assertThat(json, is("{\"myPlatform\":{\"mySensor\":{\"myKey\":[{\"timestamp\":\"2015-01-10T10:00:00.000+0100\",\"value\":\"myValue1\"},{\"timestamp\":\"2015-01-10T10:01:00.000+0100\",\"value\":\"myValue2\"}]}}}"));
  }

  private Sample sampleFrom(DateTime timestamp, String platformIdentifier, String sensorType, Object... keyAndValue) {
    TransferSample sample = new TransferSample(timestamp, platformIdentifier, sensorType);
    for (int i = 0; i < (keyAndValue.length / 2); i=i+2) {
      sample.addItem((String) keyAndValue[i], (java.io.Serializable) keyAndValue[i+1]);
    }

    return sample;
  }

  @Test
  public void shouldStoreNoMoreSamplesThanMaximum() {
    // given
    Sample s1 = sampleFrom(d1, PLATFORM, "mySensor", "myKey", "myValue1");
    Sample s2 = sampleFrom(d2, PLATFORM, "mySensor", "myKey", "myValue2");
    Sample s3 = sampleFrom(d3, PLATFORM, "mySensor", "myKey", "myValue3");
    Sample s4 = sampleFrom(d4, PLATFORM, "mySensor", "myKey", "myValue4");
    Sample s5 = sampleFrom(d5, PLATFORM, "mySensor", "myKey", "myValue5");

    TreeMap<SampleCollection.SampleTuple, String> tuples = Maps.newTreeMap();

    // when
    sut.addSampleToTuples(s1, tuples, "myValue1");
    sut.addSampleToTuples(s2, tuples, "myValue2");
    sut.addSampleToTuples(s3, tuples, "myValue3");
    sut.addSampleToTuples(s4, tuples, "myValue4");
    sut.addSampleToTuples(s5, tuples, "myValue5");

    // then
    assertThat(tuples.size(), is(3));
    assertThat(tuples.firstKey().timestamp, is(d3));
    assertThat(tuples.lastKey().timestamp, is(d5));
  }

}
