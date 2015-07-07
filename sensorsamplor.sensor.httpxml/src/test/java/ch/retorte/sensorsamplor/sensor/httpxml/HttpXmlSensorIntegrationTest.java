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
  URL sampleUrl;

  @Before
  public void setup() {
    sampleUrl = getClass().getClassLoader().getResource("sample.xml");
  }

  private HttpXmlSensorSource sourceWith(String name, URL url, String xpath) {
    return new HttpXmlSensorSource(name, url.toString(), new String[] { xpath });
  }

  @Test
  public void shouldFetchXmlData() throws SensorException {
    // given
    HttpXmlSensorSource source = sourceWith("name", sampleUrl, "current/temperature/@value");

    // when
    Sample sample = sut.measureWith(source);

    // then
    assertThat(sample.getPlatformIdentifier(), is("test"));
    assertThat(sample.getData().get("current/temperature/@value"), is("28.91"));
  }
}
