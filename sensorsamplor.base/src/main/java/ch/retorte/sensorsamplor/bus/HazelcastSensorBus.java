package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;
import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.*;

import java.util.List;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;

/**
 * Replicates sent samples to all nodes in the cluster and also retrieves them.
 */
public class HazelcastSensorBus implements SensorBus {

  private static final int BUFFER_SIZE = 4096;

  private RingBuffer<Sample> sampleBuffer;

  public HazelcastSensorBus(String nodeName, String busName, String username, String password, List<String> networkInterfaces) {
    initializeWith(nodeName, busName, username, password, networkInterfaces);
  }

  public void initializeWith(String nodeName, String busName, String username, String password, List<String> networkInterfaces) {
    Config config = createConfigWith(nodeName, username, password, networkInterfaces);
    HazelcastInstance hazelcastInstance = newHazelcastInstance(config);

    IList<Sample> list = hazelcastInstance.getList(busName);
    ILock lock = hazelcastInstance.getLock(busName);

    createBufferWith(list, lock);
  }

  private Config createConfigWith(String nodeName, String username, String password, List<String> networkInterfaces) {
    Config config = new Config(nodeName).setProperty("hazelcast.logging.type", "none");
    config.getGroupConfig().setName(username).setPassword(password);
    setNetworkConfigWith(config.getNetworkConfig(), networkInterfaces);
    return config;
  }

  private void setNetworkConfigWith(NetworkConfig networkConfig, List<String> networkInterfaces) {
    networkConfig.getJoin().getMulticastConfig().setEnabled(true);

    if (!networkInterfaces.isEmpty()) {
      InterfacesConfig interfacesConfig = networkConfig.getInterfaces();
      interfacesConfig.setEnabled(true);
      for (String i : networkInterfaces) {
        interfacesConfig.addInterface(i);
      }
    }
  }

  private void createBufferWith(IList<Sample> list, ILock lock) {
    sampleBuffer = new RingBuffer<>(list, lock, BUFFER_SIZE);
  }

  @Override
  public void send(Sample sample) {
    sampleBuffer.put(sample);
  }

  @Override
  public void registerSampleListener(final SampleListener sampleListener) {
    sampleBuffer.addItemListener(new ItemListenerAdapter() {

      @Override
      void sampleAdded(Sample sample) {
        sampleListener.onSampleAdded(sample);
      }
    }, true);
  }

  private abstract class ItemListenerAdapter implements ItemListener<Sample> {

    abstract void sampleAdded(Sample sample);

    @Override
    public void itemAdded(ItemEvent itemEvent) {
      Object receivedItem = itemEvent.getItem();
      if (receivedItem instanceof Sample) {
        sampleAdded((Sample) receivedItem);
      }
    }

    @Override
    public void itemRemoved(ItemEvent itemEvent) {
      // Currently we don't react here.
    }
  }
}
