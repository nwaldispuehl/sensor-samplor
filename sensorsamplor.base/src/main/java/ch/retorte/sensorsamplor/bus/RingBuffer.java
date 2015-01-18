package ch.retorte.sensorsamplor.bus;

import com.google.common.annotations.VisibleForTesting;
import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Distributed ring buffer keeping the n last items.
 */
public class RingBuffer<T extends Serializable> implements Serializable {

  private static final int BASE_BUFFER_SIZE = 128;

  private final Logger log = LoggerFactory.getLogger(RingBuffer.class);

  private final IList<T> buffer;
  private final ILock lock;
  private final int bufferSize;
  private final int maximumBufferSize;


  RingBuffer(IList<T> buffer, ILock lock, int bufferSize, int maximumBufferSize) {
    this.buffer = buffer;
    this.lock = lock;
    this.bufferSize = checkPositive(bufferSize);
    this.maximumBufferSize = checkPositive(maximumBufferSize);

    log.debug("Creating ring buffer with buffer size: {} (internal size: {}).", bufferSize, maximumBufferSize);
  }

  RingBuffer(IList<T> buffer, ILock lock, int bufferSize) {
    this(buffer, lock, bufferSize, BASE_BUFFER_SIZE + bufferSize * 2);
  }

  private int checkPositive(int bufferSize) {
    if (bufferSize <= 0) {
      throw new IllegalArgumentException("RingBuffer buffer size needs to be positive!");
    }
    return bufferSize;
  }

  synchronized void put(T t) {
    putWithLoggingAndChecks(t);
  }

  private void putWithLoggingAndChecks(T t) {
    putWithSizeCheck(t);
    log.debug("Added new buffer (size: {}) item: {}.", buffer.size(), t);
  }

  private void putWithSizeCheck(T t) {
    if (bufferIsTooLarge()) {
      reduceBufferWithLocking();
    }

    buffer.add(t);
  }

  private boolean bufferIsTooLarge() {
    return bufferIsTooLargeWith(maximumBufferSize, buffer);
  }

  @VisibleForTesting
  boolean bufferIsTooLargeWith(int size, List<T> list) {
    return size < list.size();
  }

  private void reduceBufferWithLocking() {
    try {
      if (lock.tryLock(10, SECONDS)) {
        try {
          log.debug("Entered the reduce buffer monitor area with current buffer size: {}.", buffer.size());
          /* Let's check the buffer size again to prevent race conditions. */
          if (bufferIsTooLarge()) {
            reduceBuffer();
          }
        } catch (Throwable e) {
          log.error("Problems while trying to reduce the buffer: {}.", e.getMessage(), e);
        } finally {
          lock.unlock();
        }
      } else {
        log.debug("Was not able to get lock for buffer reduction. Assuming someone else reduced it already.");
      }
    }
    catch (InterruptedException e) {
      log.warn("Buffer reduction process was interrupted: {}.", e.getMessage());
    }
  }

  /**
   * We have a bufferSize we talk publicly about, but the actual buffer is a factor larger, obeying the maximumBufferSize limit.
   * If this limit is reached, we cut away the first entries to truncate it to the bufferSize again.
   * We are doing this in the following rather inefficieny way to allow other nodes to add items while we're reducing the buffer.
   */
  private void reduceBuffer() {
    log.info("Reducing buffer from currently {} (limit: {}) to {}.", buffer.size(), maximumBufferSize, bufferSize);
    while(bufferIsTooLargeWith(bufferSize, buffer)) {
      buffer.remove(0);
    }
  }

  T get() {
    return buffer.get(buffer.size()-1);
  }

  List<T> getBuffer() {
      return copySubListOf(buffer, bufferSize);
  }

  @VisibleForTesting
  List<T> copySubListOf(List<T> list, int extractSize) {
    List<T> result = newArrayList(list);
    int s = result.size();
    if (s <= extractSize) {
      return result;
    }

    return result.subList(s - 1 - extractSize, s - 1);
  }

  void addItemListener(ItemListener<T> itemListener) {
    buffer.addItemListener(itemListener, true);
  }
}
