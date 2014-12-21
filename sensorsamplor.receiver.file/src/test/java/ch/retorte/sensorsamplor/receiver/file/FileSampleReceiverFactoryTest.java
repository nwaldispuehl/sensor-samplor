package ch.retorte.sensorsamplor.receiver.file;

import ch.retorte.sensorsamplor.bus.SampleListener;
import ch.retorte.sensorsamplor.bus.SensorBus;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

/**
 * Created by nw on 20.12.14.
 */
public class FileSampleReceiverFactoryTest {

  private String logFilePath = "some/path";
  private FileSampleReceiverFactory sut = new FileSampleReceiverFactory(logFilePath);

  @Test
  public void shouldProduceFileSampleReceiver() {
    // when
    SampleReceiver receiver = sut.createReceiver();

    // then
    Assert.assertTrue(receiver instanceof FileSampleReceiver);
  }
}
