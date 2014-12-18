package ch.retorte.sensorsamplor.bus;

import com.hazelcast.core.*;


/**
 * Created by nw on 18.12.14.
 */
public class Consumer extends Actor {

    private static final String NODE_NAME = "Consumer";

    public Consumer(String queueName) {
        super(queueName);
    }

    @Override
    protected String getNodeName() {
        return NODE_NAME;
    }


    public void start() {
        getBuffer().addItemListener(new ItemListener<Payload>() {

            @Override
            public void itemAdded(ItemEvent<Payload> item) {
                Payload p = item.getItem();
                System.out.println("New item added: " + p.getSomeValue() + " (Coming from: " + p.getOrigin() + ")");
                System.out.println("Buffer: " + getBuffer());
            }

            @Override
            public void itemRemoved(ItemEvent<Payload> item) {
                System.out.println("Item removed: " + item);
            }

        }, true);
    }
}
