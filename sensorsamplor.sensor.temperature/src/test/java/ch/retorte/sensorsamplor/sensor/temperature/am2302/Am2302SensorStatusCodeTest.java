package ch.retorte.sensorsamplor.sensor.temperature.am2302;

import org.junit.Test;

import static ch.retorte.sensorsamplor.sensor.temperature.am2302.Am2302SensorStatusCode.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Am2302SensorStatusCodeTest {

  @Test
  public void shouldGetStatusByValue() {
    assertThat(valueOfStatus(0), is(DHT_SUCCESS));
    assertThat(valueOfStatus(-1), is(DHT_ERROR_TIMEOUT));
    assertThat(valueOfStatus(-2), is(DHT_ERROR_CHECKSUM));
    assertThat(valueOfStatus(-3), is(DHT_ERROR_ARGUMENT));
    assertThat(valueOfStatus(-4), is(DHT_ERROR_GPIO));
  }
}
