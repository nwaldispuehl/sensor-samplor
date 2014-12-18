package ch.retorte.sensorsamplor.bus;

/**
 * Created by nw on 18.12.14.
 */
public class HazelcastTest {

    private static final String QUEUE_NAME = "myQueue";

    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("Please provide either 'p' or 'c' as argument.");
            return;
        }


        if (args[0].equals("p")) {
            System.out.println("Starting producer.");
            Producer producer = new Producer(QUEUE_NAME);
            producer.initialize();
            producer.start();
        }
        else {
            Consumer consumer = new Consumer(QUEUE_NAME);
            System.out.println("Starting consumer.");
            consumer.initialize();
            consumer.start();
        }
    }
}
