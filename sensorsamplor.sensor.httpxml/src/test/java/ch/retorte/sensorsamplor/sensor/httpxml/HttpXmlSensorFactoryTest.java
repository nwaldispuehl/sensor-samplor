package ch.retorte.sensorsamplor.sensor.httpxml;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit test for the http xml sensor factory.
 */
public class HttpXmlSensorFactoryTest {

  HttpXmlSensorFactory sut = new HttpXmlSensorFactory();

  @Test
  public void shouldParseSource() {
    // given
    String source = "myName;http://myServer/myPath;root/leave";

    // when
    HttpXmlSensorSource httpXmlSensorSource = sut.parseSourceOf(source);

    // then
    assertThat(httpXmlSensorSource.getName(), is("myName"));
    assertThat(httpXmlSensorSource.getUrl().toString(), is("http://myServer/myPath"));
    assertThat(httpXmlSensorSource.getXPaths().size(), is(1));
    assertThat(httpXmlSensorSource.getXPaths().get(0), is("root/leave"));
  }
}
