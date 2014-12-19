package ch.retorte.sensorsamplor.bus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by nw on 18.12.14.
 */
public class Producer extends HazelcastSensorBus implements Runnable {

    private static final String NODE_NAME = "Producer";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Random random = new Random();

    public Producer(String queueName) {
        super(queueName);
    }

    @Override
    protected String getNodeName() {
        return NODE_NAME;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this, 0, 3, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            System.out.println("Creating new item.");
            getBuffer().put(new Payload(InetAddress.getLocalHost().getHostName(), random.nextInt(100), random.nextBoolean()));
            System.out.println("Buffer State: " + getBuffer());
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
