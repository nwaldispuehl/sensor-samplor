package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Distributed map keeping the last item per node and sensor.
 */
public class DistributedMap implements Serializable {

  private static final String ID_DELIMITER = "-";

  private final Logger log = LoggerFactory.getLogger(DistributedMap.class);

  private final IMap<String, Sample> map;

  public DistributedMap(IMap<String, Sample> map) {
    this.map = map;

    log.debug("Creating distributed map.");
  }

  synchronized void put(Sample t) {
    map.put(createKeyOf(t), t);
  }

  private String createKeyOf(Sample sample) {
    return sample.getPlatformIdentifier() + ID_DELIMITER + sample.getSensorType();
  }

  void addEntryListener(EntryListener<String, Sample> entryListener) {
    map.addEntryListener(entryListener, true);
  }

  public List<Sample> getCurrentEntries() {
    return newArrayList(map.values());
  }
}
