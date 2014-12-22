package ch.retorte.sensorsamplor.receiver.file;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import org.junit.Test;

import java.util.Map;

import static ch.retorte.sensorsamplor.receiver.file.FileSampleReceiverFactory.LOGGING_DIRECTORY;
import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for file sample receiver factory.
 */
public class FileSampleReceiverFactoryTest {

  private FileSampleReceiverFactory sut = new FileSampleReceiverFactory();

  @Test
  public void shouldProduceFileSampleReceiver() {
    // given
    Map<String, String> config = newHashMap();
    config.put(LOGGING_DIRECTORY, "abc");
    sut.setConfigurationValues(config);

    // when
    SampleReceiver receiver = sut.createReceiver();

    // then
    assertTrue(receiver instanceof FileSampleReceiver);
  }
}
