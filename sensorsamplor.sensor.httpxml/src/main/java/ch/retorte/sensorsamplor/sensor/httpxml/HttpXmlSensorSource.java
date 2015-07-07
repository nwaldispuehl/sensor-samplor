package ch.retorte.sensorsamplor.sensor.httpxml;

import com.google.common.collect.Lists;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Source for a single sensor measurement of the http xml sensor.
 */
public class HttpXmlSensorSource {

  private String name;
  private URL url;
  private List<String> xPaths = newArrayList();


  public HttpXmlSensorSource(String name, String url, String[] xPaths) {
    this.name = name;

    try {
      this.url = new URL(url);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    setPaths(xPaths);
  }

  private void setPaths(String[] paths) {
    this.xPaths.addAll(Lists.newArrayList(paths));
  }

  public String getName() {
    return name;
  }

  public URL getUrl() {
    return url;
  }

  public List<String> getXPaths() {
    return xPaths;
  }

  @Override
  public String toString() {
    return name + " " + url + " " + xPaths;
  }
}
