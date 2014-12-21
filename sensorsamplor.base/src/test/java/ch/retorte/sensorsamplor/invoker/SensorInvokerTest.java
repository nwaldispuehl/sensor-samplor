package ch.retorte.sensorsamplor.invoker;

import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.*;

/**
 * Tests for the sensor invoker.
 */
public class SensorInvokerTest {

  Sample sample = mock(Sample.class);
  Sensor sensor = mock(Sensor.class);
  SensorInvoker sut = spy(new SensorInvoker(mock(SensorBus.class), newArrayList(sensor)));
  SensorInvoker.SensorRunner threadUnderTest = sut.createRunnerWith(sensor);

  @Before
  public void setup() {

  }

  @Test
  public void shouldProcess() throws SensorException {
    // given
    when(sensor.measure()).thenReturn(sample);

    // when
    threadUnderTest.invokeSensor(sensor);

    // then
    verify(sut).process(sample);
  }

  @Test
  public void shouldProcessError() throws SensorException {
    // given
    when(sensor.measure()).thenThrow(SensorException.class);

    // when
    threadUnderTest.invokeSensor(sensor);

    // then
    verify(sut).processError(any(SensorException.class));
  }
}
