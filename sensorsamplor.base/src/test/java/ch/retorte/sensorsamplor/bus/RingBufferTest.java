package ch.retorte.sensorsamplor.bus;

import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for the ring buffer.
 */
public class RingBufferTest {

  IList<Serializable> list = mock(IList.class);
  ILock lock = mock(ILock.class);
  RingBuffer<Serializable> sut = new RingBuffer<>(list, lock, 4);

  @Test
  public void shouldPutNewItemsAtFront() {
    // given
    Serializable item = mock(Serializable.class);

    // when
    sut.put(item);

    // then
    verify(list).add(0, item);
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
  public void shouldRemoveTailElementOfList() {
    // given
    when(list.size()).thenReturn(36);

    // when
    sut.removeListTailOf(list);

    // then
    verify(list).remove(35);
  }
}
