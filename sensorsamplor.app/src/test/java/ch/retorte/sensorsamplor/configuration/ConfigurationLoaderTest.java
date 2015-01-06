package ch.retorte.sensorsamplor.configuration;

import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static java.lang.Math.max;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit test for ConfigurationLoader.
 */
public class ConfigurationLoaderTest {

  private ConfigurationLoader sut = new ConfigurationLoader(mock(Properties.class));

  @Test
  public void shouldParseStringListProperties() {
    assertIsParsedAs(null, (String[]) null);
    assertIsParsedAs("", new String[0]);
    assertIsParsedAs("abc", "abc");
    assertIsParsedAs("a,b,c", "a", "b", "c");
  }

  private void assertIsParsedAs(String input, String... output) {
    List<String> items = sut.getStringListPropertyOf(input);
    if (items == null) {
      assertThat(output, is(nullValue()));
    }
    else {
     for (int i = 0; i < max(items.size(), output.length); i++) {
       assertThat(items.get(i), is(output[i]));
     }
    }
  }
}
