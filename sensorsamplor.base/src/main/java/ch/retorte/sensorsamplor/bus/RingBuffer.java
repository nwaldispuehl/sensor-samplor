package ch.retorte.sensorsamplor.bus;

import com.google.common.annotations.VisibleForTesting;
import com.hazelcast.core.*;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static com.google.common.base.Joiner.on;

/**
 * Distributed ring buffer keeping the n last items.
 */
public class RingBuffer<T extends Serializable> implements Serializable {

  private IList<T> list;
  private Lock lock;
  private int bufferSize;

  RingBuffer(IList<T> list, Lock lock, int bufferSize) {
    this.list = list;
    this.lock = lock;
    this.bufferSize = bufferSize;
  }

  void put(T t) {
    lock.lock();
    try {
      while (listIsTooLarge()) {
        removeListTail();
      }
      addAtFront(t);
    }
    catch (Throwable e) {
      // TODO introduce logging.
      e.printStackTrace();
    }
    finally {
      lock.unlock();
    }
  }

  private boolean listIsTooLarge() {
    return !hasSpaceLeft(list, bufferSize);
  }

  @VisibleForTesting
  boolean hasSpaceLeft(List<T> l, int maximumSize) {
    return l.size() < maximumSize;
  }

  private void removeListTail() {
    removeListTailOf(list);
  }

  @VisibleForTesting
  void removeListTailOf(List<T> l) {
    l.remove(l.size() - 1);
  }

  private void addAtFront(T t) {
    addAtFrontIn(list, t);
  }

  @VisibleForTesting
  void addAtFrontIn(List<T> l, T t) {
    l.add(0, t);
  }

  T get() {
    return list.get(0);
  }

  List<T> getBuffer() {
    return list;
  }

  @Override
  public String toString() {
    return "[" + on(",").join(list.toArray()) + "]";
  }

  void addItemListener(ItemListener<T> itemListener, boolean includeValue) {
    list.addItemListener(itemListener, includeValue);
  }
}
