package ch.retorte.sensorsamplor.sensor.temperature.am2302;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import ch.retorte.sensorsamplor.sensor.TransferSample;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ch.retorte.sensorsamplor.sensor.temperature.am2302.Am2302SensorStatusCode.*;

/**
 * Implementation of the temperature/humidity sensor which uses a native c library for communicating with the DHT22/AM2302 sensor. Is not thread safe, that is, must be used only by one instance.
 */
public class Am2302Sensor implements Sensor {

  public static final String IDENTIFIER = "temperature";

  private static final String PI_DHT_LIBRARY_NAME = "PiDht";
  private static final int SENSOR_TYPE = 22;
  private static final int FLOAT_SIZE = 16;
  private static final int RETRIES_IN_ERROR_CASE = 4;

  private final Logger log = LoggerFactory.getLogger(Am2302Sensor.class);

  private final String platformIdentifier;
  private final int pin;

  private final Pointer humidity = new Memory(FLOAT_SIZE);
  private final Pointer temperature = new Memory(FLOAT_SIZE);

  private int returnCode;
  private int triesSoFar;

  private final Am2302SensorLibrary sensorLibrary;

  private interface Am2302SensorLibrary extends Library {
    public int pi_dht_read(int sensor, int pin, Pointer humidity, Pointer temperature);
  }

  private Am2302SensorLibrary loadLibrary() {
    return (Am2302SensorLibrary) Native.loadLibrary(PI_DHT_LIBRARY_NAME, Am2302SensorLibrary.class);
  }

  public Am2302Sensor(String platformIdentifier, int pin) {
    this.platformIdentifier = platformIdentifier;
    this.pin = pin;
    this.sensorLibrary = loadLibrary();

    log.info("Created AM2302 sensor on data pin: {}.", pin);
  }

  @Override
  public Sample measure() throws SensorException {
    measureWithRetries();

    if (measurementFailed()) {
      log.warn("Temperature measurement failed. Reason according to sensor: {}.", messageOfStatus(returnCode));
      throw new SensorException(platformIdentifier, IDENTIFIER, messageOfStatus(returnCode));
    }

    log.debug("Performed measurement with values: {}, {}.", toDouble(temperature), toDouble(humidity));
    return new TransferSample(platformIdentifier, IDENTIFIER)
        .addItem("temperature", toOneDigitDouble(toDouble(temperature)))
        .addItem("humidity", toOneDigitDouble(toDouble(humidity)));
  }

  /**
   * Attempts to perform a measurement for a number of times until it succeeds or the number of maximum tries is reached.
   */
  private void measureWithRetries() {
    returnCode = -1;
    triesSoFar = 0;

    while (measurementFailed() && hasTriesLeft()) {
      triesSoFar++;
      returnCode = sensorLibrary.pi_dht_read(SENSOR_TYPE, pin, humidity, temperature);

      if (measurementFailed()) {
        sleepFor(100);
      }
    }
  }

  private boolean hasTriesLeft() {
    return triesSoFar <= RETRIES_IN_ERROR_CASE;
  }

  private boolean measurementFailed() {
    return returnCode != DHT_SUCCESS.code();
  }

  private double toDouble(Pointer pointer) {
    return pointer.getFloat(0);
  }

  private double toOneDigitDouble(Double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }

  private void sleepFor(long milliSeconds) {
    try {
      Thread.sleep(milliSeconds);
    } catch (InterruptedException e) {
      // nop
    }
  }
}
