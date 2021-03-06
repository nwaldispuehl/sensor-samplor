package ch.retorte.sensorsamplor.receiver.graphite;

import ch.retorte.sensorsamplor.receiver.SampleReceiver;
import ch.retorte.sensorsamplor.sensor.ErrorSample;
import ch.retorte.sensorsamplor.sensor.Sample;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Forwards all incoming samples to a graphite (resp. its carbon backend) instance (provided by configuration). The path consists of the node id and the sensor type.
 *
 * This is an extract from the graphite documentation:
 * <pre>
 * *The Graphite Message Format*
 *
 * All graphite messages are of the following form.
 *
 *     metric_path value timestamp\n
 *
 * So for example, "foo.bar.baz 42 74857843" where the last number is a UNIX epoch time.
 * </pre>
 */
public class GraphiteSampleReceiver implements SampleReceiver {

  public static final String IDENTIFIER = "graphite";

  private static final String GRAPHITE_FIELD_DELIMITER = " ";
  private static final String GRAPHITE_PATH_DELIMITER = ".";

  private final Logger log = LoggerFactory.getLogger(GraphiteSampleReceiver.class);
  private final String carbonServerUrl;
  private final int carbonServerPort;
  private final String carbonServerUsername;
  private final String carbonServerPassword;

  public GraphiteSampleReceiver(String carbonServerUrl, int carbonServerPort, String carbonServerUsername, String carbonServerPassword) {
    this.carbonServerUrl = carbonServerUrl;
    this.carbonServerPort = carbonServerPort;
    this.carbonServerUsername = carbonServerUsername;
    this.carbonServerPassword = carbonServerPassword;
  }

  @Override
  public void processSample(List<Sample> sampleBuffer, Sample sample) {
    sendToCarbon(sample);
  }

  private void sendToCarbon(Sample sample) {
    sendToCarbon(graphiteFormatOf(sample));
  }

  private void sendToCarbon(List<String> payloadLines) {
    log.debug("Sending message to carbon [{}:{}]: {}.", carbonServerUrl, carbonServerPort, payloadLines);

    Socket socket = null;
    OutputStream outputStream = null;
    PrintWriter printWriter = null;
    try {
      socket = new Socket(carbonServerUrl, carbonServerPort);
      outputStream = socket.getOutputStream();
      printWriter = new PrintWriter(outputStream, true);

      for (String line : payloadLines) {
        printWriter.println(line);
      }

      log.debug("Message successfully sent.");
    }
    catch (UnknownHostException e) {
      log.error("Host {}:{} not known: {}", carbonServerUrl, carbonServerPort, e.getMessage());
    }
    catch (IOException e) {
      log.error("Problems when connecting to host {}:{}: {}", carbonServerUrl, carbonServerPort, e.getMessage());
    }
    finally {
      gracefullyClose(printWriter);
      gracefullyClose(outputStream);
      gracefullyClose(socket);
    }
  }

  private void gracefullyClose(Closeable c) {
    if (c != null) {
      try {
        c.close();
      }
      catch (Exception e) {
        log.warn("Was not able to close {}. Reason: {}.", c.getClass().getSimpleName(), e.getMessage());
      }
    }
  }

  private List<String> graphiteFormatOf(Sample sample) {
    List<String> result = newArrayList();
    for (String key : sample.getData().keySet()) {
      result.add(graphiteFormatOf(sample, key));
    }
    return result;
  }

  @VisibleForTesting
  String graphiteFormatOf(Sample sample, String key) {
    return pathOf(sample, key) + GRAPHITE_FIELD_DELIMITER + valueOf(sample, key) + GRAPHITE_FIELD_DELIMITER + timestampOf(sample);
  }

  @VisibleForTesting
  String pathOf(Sample sample, String key) {
    return sample.getPlatformIdentifier() + GRAPHITE_PATH_DELIMITER + sample.getSensorType() + GRAPHITE_PATH_DELIMITER + key;
  }

  @VisibleForTesting
  String valueOf(Sample sample, String key) {
    return sample.getData().get(key).toString();
  }

  @VisibleForTesting
  String timestampOf(Sample sample) {
    return String.valueOf(sample.getTimestamp().getMillis() / 1000);
  }

  @Override
  public void processError(List<Sample> sampleBuffer, ErrorSample errorSample) {
    // We don't send errors at the moment.
  }
}
