package ch.retorte.sensorsamplor.sensor.temperature.adm2302;

import ch.retorte.sensorsamplor.sensor.temperature.am2302.Am2302SensorStatusCode;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static ch.retorte.sensorsamplor.sensor.temperature.am2302.Am2302SensorStatusCode.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Am2302SensorStatusCodeTest {

  @Test
  public void shouldGetStatusByValue() {
    assertThat(valueOfStatus(0), CoreMatchers.is(DHT_SUCCESS));
    assertThat(valueOfStatus(-1), CoreMatchers.is(DHT_ERROR_TIMEOUT));
    assertThat(valueOfStatus(-2), CoreMatchers.is(DHT_ERROR_CHECKSUM));
    assertThat(valueOfStatus(-3), CoreMatchers.is(DHT_ERROR_ARGUMENT));
    assertThat(valueOfStatus(-4), CoreMatchers.is(DHT_ERROR_GPIO));
  }
}
