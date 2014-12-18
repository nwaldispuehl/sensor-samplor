package ch.retorte.sensorsamplor.bus;

import com.hazelcast.config.Config;
import com.hazelcast.core.*;

/**
 * Created by nw on 18.12.14.
 */
public abstract class Actor {

    private RingBuffer<Payload> buffer;
    private String queueName;

    protected Actor(String queueName) {
        this.queueName = queueName;
    }

    public void initialize() {
        Config config = new Config(getNodeName());
        config.setProperty("hazelcast.logging.type", "none");

        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
        config.getNetworkConfig().getInterfaces().setEnabled(true).addInterface("10.100.10.*").addInterface("192.168.1.*");

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        hazelcastInstance.getCluster().addMembershipListener(new MyMembershipListener());

        IList<Payload> list = hazelcastInstance.getList(queueName);
        ILock lock = hazelcastInstance.getLock(queueName);

        buffer = new RingBuffer<>(list, lock, 20);
    }

    protected abstract String getNodeName();

    protected RingBuffer<Payload> getBuffer() {
        return buffer;
    }

    private class MyMembershipListener implements MembershipListener {

        @Override
        public void memberAdded(MembershipEvent membershipEvent) {
            System.out.println("Member added: " + membershipEvent.getMember());
        }

        @Override
        public void memberRemoved(MembershipEvent membershipEvent) {
            System.out.println("Member left: " + membershipEvent.getMember());
        }

        @Override
        public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
            // nop
        }
    }
}
