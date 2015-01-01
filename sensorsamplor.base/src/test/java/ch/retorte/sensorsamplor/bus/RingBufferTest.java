package ch.retorte.sensorsamplor.bus;

import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IFunction;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.Serializable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit test for the ring buffer.
 */
public class RingBufferTest {

  private final IList<Serializable> list = mock(IList.class);
  private final ILock lock = mock(ILock.class);
  private final IAtomicLong nextPosition = mock(IAtomicLong.class);
  private final RingBuffer<Serializable> sut = new RingBuffer<>(list, lock, nextPosition, 4);

  @Test
  public void shouldAppendToList() {
    // given
    Serializable item = mock(Serializable.class);

    // when
    sut.appendToList(item);

    // then
    verify(list).add(item);
  }

  @Test
  public void shouldDetermineIfTooLarge() {
    // given
    when(list.size()).thenReturn(4);

    // when/then
    assertFalse(sut.hasSpaceLeft(list, 2));
    assertFalse(sut.hasSpaceLeft(list, 3));
    assertFalse(sut.hasSpaceLeft(list, 4));
    assertTrue(sut.hasSpaceLeft(list, 5));
  }

  @Test
  public void shouldAddItemToNextPosition() {
    // given
    Serializable item = mock(Serializable.class);
    when(nextPosition.getAndAlter(Matchers.<IFunction<Long, Long>>any())).thenReturn(3l);

    // when
    sut.putToNextPosition(item);

    // then
    verify(list).set(3, item);
  }
}
