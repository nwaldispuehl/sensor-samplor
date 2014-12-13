package ch.retorte.pitempstation.invoker;

import ch.retorte.pitempstation.receiver.SampleReceiver;
import ch.retorte.pitempstation.sensor.Sample;
import ch.retorte.pitempstation.sensor.Sensor;
import ch.retorte.pitempstation.sensor.SensorException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the sensor invoker.
 */
public class SensorInvokerTest {

  Sensor sensor = mock(Sensor.class);
  SampleReceiver receiver = mock(SampleReceiver.class);
  SensorInvoker sut = new SensorInvoker(sensor);

  @Before
  public void setup() {
    sut.registerReceiver(receiver);
  }

  @Test
  public void shouldProcess() throws SensorException {
    // given
    when(sensor.measure()).thenReturn(mock(Sample.class));

    // when
    sut.invokeSensor();

    // then
  }

  @Test
  public void shouldProcessError() throws SensorException {
    // given

    // when
    sut.invokeSensor();

    // then
  }
}
