package ch.retorte.sensorsamplor.bus;

import com.hazelcast.core.*;

import java.io.Serializable;

import static com.google.common.base.Joiner.on;

/**
 * Distributed ring buffer keeping the n last items.
 */
public class RingBuffer<T extends Serializable> implements Serializable {

  private IList<T> list;
  private ILock lock;
  private int bufferSize;

  public RingBuffer(IList<T> list, ILock lock, int bufferSize) {
    this.list = list;
    this.lock = lock;
    this.bufferSize = bufferSize;
  }

  public void put(T t) {
    lock.lock();
    list.add(0, t);
    while (bufferSize < list.size()) {
      list.remove(list.get(list.size() - 1));
    }
    lock.unlock();
  }

  public T get() {
    return list.get(0);
  }

  @Override
  public String toString() {
    return "[" + on(",").join(list.toArray()) + "]";
  }

  public void addItemListener(ItemListener<T> itemListener, boolean includeValue) {
    list.addItemListener(itemListener, includeValue);
  }
}
