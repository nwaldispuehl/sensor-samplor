package ch.retorte.sensorsamplor.receiver.graphite;

import ch.retorte.sensorsamplor.receiver.ReceiverFactory;
import ch.retorte.sensorsamplor.receiver.SampleReceiver;

import java.util.Collection;
import java.util.Map;

import static ch.retorte.sensorsamplor.receiver.graphite.GraphiteSampleReceiver.IDENTIFIER;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Creates a graphite sample receiver.
 */
public class GraphiteSampleReceiverFactory implements ReceiverFactory {

  private static final String GRAPHITE_CARBON_SERVER_URL = "sensorsamplor.receiver.graphite.carbon.server.url";
  private static final String GRAPHITE_CARBON_SERVER_PORT = "sensorsamplor.receiver.graphite.carbon.server.port";
  private static final String GRAPHITE_CARBON_SERVER_USERNAME = "sensorsamplor.receiver.graphite.carbon.server.username";
  private static final String GRAPHITE_CARBON_SERVER_PASSWORD = "sensorsamplor.receiver.graphite.carbon.server.password";

  private String carbonServerUrl;
  private int carbonServerPort = 2003;
  private String carbonServerUsername;
  private String carbonServerPassword;

  @Override
  public SampleReceiver createReceiver() {
    return new GraphiteSampleReceiver(carbonServerUrl, carbonServerPort, carbonServerUsername, carbonServerPassword);
  }

  @Override
  public Collection<String> getConfigurationKeys() {
    return newArrayList(
        GRAPHITE_CARBON_SERVER_URL,
        GRAPHITE_CARBON_SERVER_PORT,
        GRAPHITE_CARBON_SERVER_USERNAME,
        GRAPHITE_CARBON_SERVER_PASSWORD
    );
  }

  @Override
  public void setConfigurationValues(Map<String, String> configuration) {
    if (configuration.containsKey(GRAPHITE_CARBON_SERVER_URL)) {
      carbonServerUrl = configuration.get(GRAPHITE_CARBON_SERVER_URL);
    }
    if (configuration.containsKey(GRAPHITE_CARBON_SERVER_PORT) && !configuration.get(GRAPHITE_CARBON_SERVER_PORT).isEmpty()) {
      carbonServerPort = Integer.valueOf(configuration.get(GRAPHITE_CARBON_SERVER_PORT));
    }
    if (configuration.containsKey(GRAPHITE_CARBON_SERVER_USERNAME)) {
      carbonServerUsername = configuration.get(GRAPHITE_CARBON_SERVER_USERNAME);
    }
    if (configuration.containsKey(GRAPHITE_CARBON_SERVER_PASSWORD)) {
      carbonServerPassword = configuration.get(GRAPHITE_CARBON_SERVER_PASSWORD);
    }
  }

  @Override
  public String getIdentifier() {
    return IDENTIFIER;
  }
}
