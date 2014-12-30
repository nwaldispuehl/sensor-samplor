package ch.retorte.sensorsamplor.receiver.file;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for the file sample receiver.
 */
public class FileSampleReceiverTest {

  private static final String LOG_FILE_PATH = "/tmp/";

  private final FileSampleReceiver sut = new FileSampleReceiver(LOG_FILE_PATH);

  @Test
  public void shouldFormatDate() {
    assertThat(sut.format(date(2014, 3, 31, 14, 45, 12)), is("2014-03-31"));
  }

  private DateTime date(int year, int month, int day, int hour, int minute, int second) {
    return new DateTime(year, month, day, hour, minute, second);
  }

  @Test
  public void shouldCheckForTrailingSlash() {
    assertThat(sut.checkForTrailingDelimiter("/i/like/ham"), is("/i/like/ham/"));
    assertThat(sut.checkForTrailingDelimiter("/i/adore/coffee/"), is("/i/adore/coffee/"));
  }
}
