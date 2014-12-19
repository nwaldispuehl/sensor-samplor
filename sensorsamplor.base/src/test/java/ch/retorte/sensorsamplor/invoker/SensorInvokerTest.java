package ch.retorte.sensorsamplor.invoker;

import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * Tests for the sensor invoker.
 */
public class SensorInvokerTest {

  Sensor sensor = mock(Sensor.class);
  SampleReceiver receiver = mock(SampleReceiver.class);
  SensorsInvoker sut = spy(new SensorsInvoker(Lists.<Sensor>newArrayList(), Mockito.mock(SensorBus.class)));

  @Before
  public void setup() {

  }

  @Test
  public void shouldProcess() throws SensorException {
    // given
    when(sensor.measure()).thenReturn(mock(Sample.class));

    // when
    sut.invokeSensors();

    // then
    verify(sut).process(any(Sample.class));
  }

  @Test
  public void shouldProcessError() throws SensorException {
    // given
    when(sensor.measure()).thenThrow(SensorException.class);

    // when
    sut.invokeSensors();

    // then
    verify(sut).processError(any(SensorException.class));
  }
}
