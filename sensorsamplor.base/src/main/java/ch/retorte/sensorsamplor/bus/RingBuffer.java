package ch.retorte.sensorsamplor.bus;

import com.google.common.annotations.VisibleForTesting;
import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Distributed ring buffer keeping the n last items.
 */
public class RingBuffer<T extends Serializable> implements Serializable {

  private final Logger log = LoggerFactory.getLogger(RingBuffer.class);

  private final IList<T> list;
  private final ILock lock;
  private final IAtomicLong nextPosition;
  private final Integer bufferSize;

  private ModularIncrementFunction modularIncrement;

  RingBuffer(IList<T> list, ILock lock, IAtomicLong nextPosition, int bufferSize) {
    this.list = list;
    this.lock = lock;
    this.nextPosition = nextPosition;
    this.bufferSize = bufferSize;
    this.modularIncrement = new ModularIncrementFunction(bufferSize);

    log.debug("Creating ring buffer with buffer size: {}.", bufferSize);
  }

  void put(T t) {
    try {
      if (lock.tryLock(10, SECONDS)) {
        try {
          putIntoBuffer(t);
        } catch (Throwable e) {
          log.error("Problems while trying to add item to buffer: {}.", e.getMessage(), e);
        } finally {
          lock.unlock();
        }
        log.debug("Added new buffer item: {}.", t);

      } else {
        log.warn("Was not able to add item to buffer due to locked buffer. Giving up for this time.");
      }
    }
    catch (InterruptedException e) {
      log.warn("Thread was interrupted: {}.", e.getMessage());
    }
  }

  private void putIntoBuffer(T t) {
    if (isListFull()) {
      putToNextPosition(t);
    } else {
      appendToList(t);
    }
  }

  @VisibleForTesting
  void putToNextPosition(T t) {
    long newId = nextPosition.getAndAlter(modularIncrement);
    list.set((int) newId, t);
  }

  @VisibleForTesting
  void appendToList(T t) {
    nextPosition.alter(modularIncrement);
    list.add(t);
  }

  T get() {
    return list.get((int) nextPosition.get());
  }

  List<T> getBuffer() {
    if (isListFull()) {
      return copyModularList();
    }
    else {
      return copyList();
    }
  }

  private boolean isListFull() {
    return !hasSpaceLeft(list, bufferSize);
  }

  @VisibleForTesting
  boolean hasSpaceLeft(List<T> l, int limit) {
    return l.size() < limit;
  }

  private List<T> copyList() {
    return newArrayList(list);
  }

  private List<T> copyModularList() {
    List<T> result = newArrayListWithCapacity(bufferSize);
    int p = (int) nextPosition.get();
    for (int i = 0; i < bufferSize; i++) {
      result.add(list.get((p + i) % bufferSize));
    }
    return list;
  }

  @Override
  public String toString() {
    return "[" + on(",").join(list.toArray()) + "]";
  }

  void addItemListener(ItemListener<T> itemListener) {
    list.addItemListener(itemListener, true);
  }
}
