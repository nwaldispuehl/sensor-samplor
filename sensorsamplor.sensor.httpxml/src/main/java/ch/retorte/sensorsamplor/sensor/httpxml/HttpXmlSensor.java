package ch.retorte.sensorsamplor.sensor.httpxml;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import ch.retorte.sensorsamplor.sensor.TransferSample;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.net.URL;

/**
 * Sensor which fetches a http service offering XML data and pushes a single node into the network.
 */
public class HttpXmlSensor implements Sensor {

  public static final String IDENTIFIER = "httpXml";

  private final String platformIdentifier;
  private HttpXmlSensorSource source;

  private final Logger log = LoggerFactory.getLogger(HttpXmlSensor.class);

  @VisibleForTesting
  HttpXmlSensor(String platformIdentifier) {
    this.platformIdentifier = platformIdentifier;
  }

  public HttpXmlSensor(String platformIdentifier, HttpXmlSensorSource source) {
    this.platformIdentifier = platformIdentifier;
    this.source = source;
  }

  @Override
  public Sample measure() throws SensorException {
    return measureWith(source());
  }

  @VisibleForTesting
  HttpXmlSensorSource source() {
    return source;
  }

  @VisibleForTesting
  Sample measureWith(HttpXmlSensorSource source) throws SensorException {
    try {
      TransferSample sample = new TransferSample(platformIdentifier, source.getName());
      Document xml = fetchFrom(source.getUrl());
      log.debug("Fetched xml from {}.", source.getUrl());

      for (String xPathExpression : source.getXPaths()) {
        sample.addItem(getIdentifierFrom(xPathExpression), getNodeContentWith(xml, xPathExpression));
      }

      log.debug("Created sample {}.", sample);

      return sample;
    }
    catch (Exception e) {
      throw new SensorException(platformIdentifier, source.getName(), e.getMessage());
    }
  }

  private String getIdentifierFrom(String path) {
    return path;
  }

  private String getNodeContentWith(Document d, String xPathExpression) throws XPathExpressionException {
    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr = xpath.compile(xPathExpression);
    NodeList nodeList = (NodeList) expr.evaluate(d, XPathConstants.NODESET);

    if (0 < nodeList.getLength()) {
      return nodeList.item(0).getNodeValue();
    }

    return "";
  }

  private Document fetchFrom(URL url) throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(url.openStream());
    document.getDocumentElement().normalize();
    return document;
  }


}
