package ch.retorte.sensorsamplor.sensor.httpxml;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.SensorException;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Integration test for the HttpXmlSensor.
 */
public class HttpXmlSensorIntegrationTest {

  HttpXmlSensor sut = spy(new HttpXmlSensor("test"));
  URL sample1Url;
  URL sample2Url;

  @Before
  public void setup() {
    sample1Url = getClass().getClassLoader().getResource("sample.xml");
    sample2Url = getClass().getClassLoader().getResource("sample2.xml");
  }

  private HttpXmlSensorSource sourceWith(String name, URL url, String xpath) {
    return new HttpXmlSensorSource(name, url.toString(), new String[] { xpath });
  }

  @Test
  public void shouldFetchXmlDataFromSample1() throws SensorException {
    // given
    HttpXmlSensorSource source = sourceWith("name", sample1Url, "current/temperature/@value");

    // when
    Sample sample = sut.measureWith(source);

    // then
    assertThat(sample.getPlatformIdentifier(), is("test"));
    assertThat((String) sample.getData().get("current/temperature/@value"), is("28.91"));
  }

  @Test
  public void shouldFetchXmlDataFromSample2() throws SensorException {
    // given
    HttpXmlSensorSource source = sourceWith("name", sample2Url, "current_observation/temp_c/text()");

    // when
    Sample sample = sut.measureWith(source);

    // then
    assertThat(sample.getPlatformIdentifier(), is("test"));
    assertThat((String) sample.getData().get("current_observation/temp_c/text"), is("23.4"));
  }
}
