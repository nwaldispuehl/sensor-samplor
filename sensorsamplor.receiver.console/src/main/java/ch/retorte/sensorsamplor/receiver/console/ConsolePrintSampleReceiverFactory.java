package ch.retorte.sensorsamplor.receiver.console;

import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Map;

import static ch.retorte.sensorsamplor.receiver.console.ConsolePrintSampleReceiver.IDENTIFIER;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Factory for the console print receiver.
 */
public class ConsolePrintSampleReceiverFactory implements ReceiverFactory {

  @Override
  public SampleReceiver createReceiver() {
    return new ConsolePrintSampleReceiver();
  }

  @Override
  public String getIdentifier() {
    return IDENTIFIER;
  }

  @Override
  public Collection<String> getConfigurationKeys() {
    return newArrayList();
  }

  @Override
  public void setConfigurationValues(Map<String, String> configuration) {
    // We don't need configuration.
  }
}
