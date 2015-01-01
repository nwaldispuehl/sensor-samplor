package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;
import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;

/**
 * Replicates sent samples to all nodes in the cluster and also retrieves them.
 */
public class HazelcastSensorBus implements SensorBus {

  private final Logger log = LoggerFactory.getLogger(HazelcastSensorBus.class);

  private HazelcastInstance hazelcastInstance;
  private RingBuffer<Sample> sampleBuffer;

  public HazelcastSensorBus(String nodeName, String busName, String username, String password, int bufferSize, List<String> networkInterfaces, List<String> remoteMembers) {
    initializeWith(nodeName, busName, username, password, bufferSize, networkInterfaces, remoteMembers);
  }

  public void initializeWith(String nodeName, String busName, String username, String password, int bufferSize, List<String> networkInterfaces, List<String> remoteMembers) {
    Config config = createConfigWith(nodeName, username, password, networkInterfaces, remoteMembers);
    hazelcastInstance = newHazelcastInstance(config);
    configureMembershipListenerFor(hazelcastInstance);

    IList<Sample> list = hazelcastInstance.getList(busName);
    ILock lock = hazelcastInstance.getLock(busName);
    IAtomicLong position = hazelcastInstance.getAtomicLong(busName);

    createBufferWith(list, lock, position, bufferSize);

    log.debug("Created HazelcastSensorBus with ID: {}, bus name: {}, user name: {}, password: {}, buffer size: {}, interfaces: {}, and remote members: {}.", nodeName, busName, username, password, bufferSize, networkInterfaces, remoteMembers);
    log.info("Total number of cluster members: {}.", hazelcastInstance.getCluster().getMembers().size());
  }

  private void configureMembershipListenerFor(HazelcastInstance hazelcastInstance) {
    hazelcastInstance.getCluster().addMembershipListener(new LoggingMembershipListener());
  }

  private Config createConfigWith(String nodeName, String username, String password, List<String> networkInterfaces, List<String> remoteMembers) {
    Config config = new Config(nodeName).setProperty("hazelcast.logging.type", "none");
    config.getGroupConfig().setName(username).setPassword(password);
    setNetworkConfigWith(config.getNetworkConfig(), networkInterfaces, remoteMembers);
    return config;
  }

  private void setNetworkConfigWith(NetworkConfig networkConfig, List<String> networkInterfaces, List<String> remoteMembers) {
    networkConfig.getJoin().getMulticastConfig().setEnabled(true);
    setNetworkInterfacesConfigWith(networkConfig.getInterfaces(), networkInterfaces);
    setTcpIpConfigWith(networkConfig.getJoin().getTcpIpConfig(), remoteMembers);
  }

  private void setNetworkInterfacesConfigWith(InterfacesConfig interfacesConfig, List<String> networkInterfaces) {
    if (!networkInterfaces.isEmpty()) {
      interfacesConfig.setEnabled(true);
      for (String i : networkInterfaces) {
        interfacesConfig.addInterface(i);
        log.debug("Added interface {} to network config.", i);
      }
    }
  }

  private void setTcpIpConfigWith(TcpIpConfig tcpIpConfig, List<String> remoteMembers) {
    if (!remoteMembers.isEmpty()) {
      tcpIpConfig.setEnabled(true);
      for (String m : remoteMembers) {
        tcpIpConfig.addMember(m);
        log.debug("Added member {} to TCP/IP config.", m);
      }
    }
  }

  private void createBufferWith(IList<Sample> list, ILock lock, IAtomicLong position, int bufferSize) {
    sampleBuffer = new RingBuffer<>(list, lock, position, bufferSize);
  }

  @Override
  public synchronized void send(Sample sample) {
    log.debug("Sending sample: {}.", sample.getId());
    sampleBuffer.put(sample);
  }

  @Override
  public void registerSampleListener(final SampleListener sampleListener) {
    sampleBuffer.addItemListener(new ItemListenerAdapter() {

      @Override
      void sampleAdded(Sample sample) {
        sampleListener.onSampleAdded(getBuffer(), sample);
      }
    });
    log.debug("Registered new sample listener: {}.", sampleListener.getClass().getSimpleName());
  }

  @Override
  public List<Sample> getBuffer() {
    return sampleBuffer.getBuffer();
  }

  @Override
  public void stop() {
    log.info("Attempting to shut down sensor bus.");
    hazelcastInstance.shutdown();
    log.info("Stopped sensor bus.");
  }

  private abstract class ItemListenerAdapter implements ItemListener<Sample> {

    abstract void sampleAdded(Sample sample);

    @Override
    public void itemAdded(ItemEvent itemEvent) {
      Object receivedItem = itemEvent.getItem();
      if (receivedItem instanceof Sample) {
        log.debug("Item added event: {}.", receivedItem);
        sampleAdded((Sample) receivedItem);
      }
    }

    @Override
    public void itemRemoved(ItemEvent itemEvent) {
      // Currently we don't react here.
      log.debug("Item removed event: {}.", itemEvent.getItem());
    }
  }

  private class LoggingMembershipListener implements MembershipListener {

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
      log.info("Cluster added member: {}. Total members now: {}.", membershipEvent.getMember(), membershipEvent.getMembers().size());
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
      log.info("Cluster removed member: {}. Total members now: {}.", membershipEvent.getMember(), membershipEvent.getMembers().size());
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
      log.info("Cluster changed attribute of member: {} to {}:{}.", memberAttributeEvent.getMember(), memberAttributeEvent.getKey(), memberAttributeEvent.getValue());
    }
  }
}
