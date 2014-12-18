package ch.retorte.sensorsamplor.bus;

import java.io.Serializable;

/**
 * Created by nw on 18.12.14.
 */
public class Payload implements Serializable {

    private final String origin;
    private final Integer someValue;
    private final Boolean someTruth;

    public Payload(String origin, Integer someValue, Boolean someTruth) {
        this.origin = origin;
        this.someValue = someValue;
        this.someTruth = someTruth;
    }

    public String getOrigin() {
        return origin;
    }

    public Integer getSomeValue() {
        return someValue;
    }

    public Boolean getSomeTruth() {
        return someTruth;
    }

    @Override
    public String toString() {
        return getSomeValue().toString();
    }
}
