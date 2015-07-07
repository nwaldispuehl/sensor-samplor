package ch.retorte.sensorsamplor.sensor.httpxml;

import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorFactory;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Factory for the HttpXmlSensor.
 */
public class HttpXmlSensorFactory implements SensorFactory {

  private static final String SOURCE = "sensorsamplor.sensor.httpxml.source";

  private final Logger log = LoggerFactory.getLogger(HttpXmlSensorFactory.class);

  private HttpXmlSensorSource source;

  @Override
  public Sensor createSensorFor(String platformIdentifier) {
    return new HttpXmlSensor(platformIdentifier, source);
  }

  @Override
  public Collection<String> getConfigurationKeys() {
    return newArrayList(SOURCE);
  }

  @Override
  public void setConfigurationValues(Map<String, String> configuration) {
    if (configuration.containsKey(SOURCE)) {
      parse(configuration.get(SOURCE));
    }
  }

  private void parse(String sourceList) {
    source = parseSourceOf(sourceList);
  }

  @VisibleForTesting
  HttpXmlSensorSource parseSourceOf(String source) {
    String[] sourceParts = source.split(";", 3);
    HttpXmlSensorSource s = new HttpXmlSensorSource(sourceParts[0], sourceParts[1], sourceParts[2].split(";"));
    log.debug("Created new http xml sensor source: {}.", s);
    return s;
  }

  @Override
  public String getIdentifier() {
    return HttpXmlSensor.IDENTIFIER;
  }
}
