package ch.retorte.sensorsamplor.bus;

import com.hazelcast.core.IFunction;

/**
 * Function implementation for the ring buffer; increments the counter modularly.
 */
public class ModularIncrementFunction implements IFunction<Long, Long> {

  private Integer limit;

  public ModularIncrementFunction(Integer limit) {
    this.limit = limit;
  }

  @Override
  public Long apply(Long input) {
    return (input + 1) % limit;
  }
}
