package ch.retorte.sensorsamplor.bus;

import ch.retorte.sensorsamplor.sensor.Sample;
import com.hazelcast.config.*;
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
  DistributedMap distributedMap;

  public HazelcastSensorBus(String nodeName, String busName, String username, String password, List<String> networkInterfaces, List<String> remoteMembers) {
    initializeWith(nodeName, busName, username, password, networkInterfaces, remoteMembers);
  }

  public void initializeWith(String nodeName, String busName, String username, String password, List<String> networkInterfaces, List<String> remoteMembers) {
    Config config = createConfigWith(nodeName, username, password, networkInterfaces, remoteMembers);
    hazelcastInstance = newHazelcastInstance(config);
    configureMembershipListenerFor(hazelcastInstance);

    IMap<String, Sample> map = hazelcastInstance.getMap(busName);

    createMapWith(map);

    log.debug("Created HazelcastSensorBus with ID: {}, bus name: {}, user name: {}, password: {}, interfaces: {}, and remote members: {}.", nodeName, busName, username, password, networkInterfaces, remoteMembers);
    log.info("Total number of cluster members: {}.", hazelcastInstance.getCluster().getMembers().size());
  }

  private void configureMembershipListenerFor(HazelcastInstance hazelcastInstance) {
    hazelcastInstance.getCluster().addMembershipListener(new LoggingMembershipListener());
  }

  private Config createConfigWith(String nodeName, String username, String password, List<String> networkInterfaces, List<String> remoteMembers) {
    Config config = new Config(nodeName)
        .setProperty("hazelcast.logging.type", "slf4j")
        .setProperty("hazelcast.max.operation.timeout", "30")
        .setProperty("hazelcast.redo.giveup.threshold", "30");

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

  private void createMapWith(IMap<String, Sample> map) {
    distributedMap = new DistributedMap(map);
  }

  @Override
  public void send(Sample sample) {
    log.debug("Attempting to submit sample: {}.", sample.getId());
    new Thread(new SampleSender(sample)).start();
  }

  private class SampleSender implements Runnable {

    private Sample sample;

    SampleSender(Sample sample) {
      this.sample = sample;
    }

    @Override
    public void run() {
      try {
        distributedMap.put(sample);
        log.debug("Submitted sample: {}.", sample.getId());
      }
      catch (RuntimeException e) {
        log.error("Failed to add sample to sampleBuffer due to: {}.", e);
      }
    }
  }

  @Override
  public void registerSampleListener(final SampleListener sampleListener) {
    distributedMap.addEntryListener(new EntryListenerAdapter() {

      @Override
      void sampleAdded(Sample sample) {
        sampleListener.onSampleAdded(sample);
      }
    });
    log.debug("Registered new sample listener: {}.", sampleListener.getClass().getSimpleName());
  }

  @Override
  public List<Sample> getBuffer() {
    return distributedMap.getCurrentEntries();
  }

  @Override
  public void stop() {
    log.info("Attempting to shut down sensor bus.");
    hazelcastInstance.shutdown();
    log.info("Stopped sensor bus.");
  }

  private abstract class EntryListenerAdapter implements EntryListener<String, Sample> {

    abstract void sampleAdded(Sample sample);

    @Override
    public void entryAdded(EntryEvent<String, Sample> event) {
      Sample sample = event.getValue();
      log.debug("Entry added event: {}.", sample);
      sampleAdded(sample);
    }

    @Override
    public void entryUpdated(EntryEvent<String, Sample> event) {
      entryAdded(event);
    }

    @Override
    public void entryRemoved(EntryEvent<String, Sample> event) {
      log.debug("Entry removed event: {}.", event.getValue());
    }

    @Override
    public void entryEvicted(EntryEvent<String, Sample> event) {
      // nop
    }

    @Override
    public void mapCleared(MapEvent event) {
      // nop
    }

    @Override
    public void mapEvicted(MapEvent event) {
      // nop
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
