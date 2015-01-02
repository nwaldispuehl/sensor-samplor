package ch.retorte.sensorsamplor.bus;

import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit test for the ring buffer.
 */
public class RingBufferTest {

  private final IList<Serializable> list = mock(IList.class);
  private final ILock lock = mock(ILock.class);
  private final RingBuffer<Serializable> sut = new RingBuffer<>(list, lock, 4, 8);

  @Test
  public void shouldAppendToList() {
    // given
    Serializable item = mock(Serializable.class);

    // when
    sut.put(item);

    // then
    verify(list).add(item);
  }

  @Test
  public void shouldDetermineIfTooLarge() {
    // given
    when(list.size()).thenReturn(4);

    // when/then
    assertTrue(sut.bufferIsTooLargeWith(2, list));
    assertTrue(sut.bufferIsTooLargeWith(3, list));
    assertFalse(sut.bufferIsTooLargeWith(4, list));
    assertFalse(sut.bufferIsTooLargeWith(5, list));
  }

  @Test
  public void shouldCopySubList() {
    assertThat(sut.copySubListOf(listOf(3), 4).size(), is(3));
    assertThat(sut.copySubListOf(listOf(4), 4).size(), is(4));
    assertThat(sut.copySubListOf(listOf(5), 4).size(), is(4));
  }

  private List<Serializable> listOf(int size) {
    List<Serializable> result = newArrayList();
    for (int i = 0 ; i < size ; i++) {
      result.add(mock(Serializable.class));
    }
    return result;
  }

}
