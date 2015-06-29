package ch.retorte.sensorsamplor.sensor.temperature.am2302;

import ch.retorte.sensorsamplor.sensor.Sample;
import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import ch.retorte.sensorsamplor.sensor.TransferSample;
import com.google.common.annotations.VisibleForTesting;
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
  private static final int WAITING_TIME_MS_BEFORE_RETRY = 250;
  private static final double ALLOWED_TEMPERATURE_VARIATION = 5;
  private static final double ALLOWED_HUMIDITY_VARIATION = 10;

  private final Logger log = LoggerFactory.getLogger(Am2302Sensor.class);

  private final String platformIdentifier;
  private final int pin;
  private final double temperatureCorrection;
  private final double humidityCorrection;

  private final Pointer temperature = new Memory(FLOAT_SIZE);
  private final Pointer humidity = new Memory(FLOAT_SIZE);

  private Double temperatureCache;
  private Double humidityCache;

  private int returnCode;
  private int triesSoFar;

  private final Am2302SensorLibrary sensorLibrary;



  /**
   * The interface for the native piDht library we're using for communication with the sensor.
   */
  private interface Am2302SensorLibrary extends Library {
    int pi_dht_read(int sensor, int pin, Pointer humidity, Pointer temperature);
  }

  private Am2302SensorLibrary loadLibrary() {
    return (Am2302SensorLibrary) Native.loadLibrary(PI_DHT_LIBRARY_NAME, Am2302SensorLibrary.class);
  }

  public Am2302Sensor(String platformIdentifier, int pin, double temperatureCorrection, double humidityCorrection) {
    this.platformIdentifier = platformIdentifier;
    this.pin = pin;
    this.temperatureCorrection = temperatureCorrection;
    this.humidityCorrection = humidityCorrection;
    this.sensorLibrary = loadLibrary();

    log.info("Created AM2302 sensor on data pin: {}.", pin);
  }

  @Override
  public Sample measure() throws SensorException {
    measureWithRetries();

    if (measurementNotSuccessful()) {
      log.warn("Temperature measurement failed. Reason according to sensor: {}.", messageOfStatus(returnCode));
      throw new SensorException(platformIdentifier, IDENTIFIER, messageOfStatus(returnCode));
    }

    cacheMeasurements();

    log.debug("Performed measurement with values: {}, {}.", toDouble(temperature), toDouble(humidity));
    return new TransferSample(platformIdentifier, IDENTIFIER)
        .addItem("temperature", toOneDigitDouble(applyTemperatureCorrection(toDouble(temperature))))
        .addItem("humidity", toOneDigitDouble(applyHumidityCorrection(toDouble(humidity))));
  }

  /**
   * Attempts to perform a measurement for a number of times until it succeeds or the number of maximum tries is reached.
   */
  private void measureWithRetries() {
    returnCode = -1;
    triesSoFar = 0;

    while ((measurementNotSuccessful() || tooFarOff()) && hasTriesLeft()) {
      triesSoFar++;
      returnCode = sensorLibrary.pi_dht_read(SENSOR_TYPE, pin, humidity, temperature);

      if (measurementNotSuccessful()) {
        sleepFor(WAITING_TIME_MS_BEFORE_RETRY);
      }
    }
  }

  private boolean hasTriesLeft() {
    return triesSoFar <= RETRIES_IN_ERROR_CASE;
  }

  private boolean measurementNotSuccessful() {
    return returnCode != DHT_SUCCESS.code();
  }

  private boolean tooFarOff() {
    return isCacheSet() && (temperatureTooFarOff() || humidityTooFarOff());
  }

  private boolean isCacheSet() {
    return temperatureCache != null;
  }

  private boolean temperatureTooFarOff() {
    return ALLOWED_TEMPERATURE_VARIATION < distanceOf(temperatureCache, toDouble(temperature));
  }

  private boolean humidityTooFarOff() {
    return ALLOWED_HUMIDITY_VARIATION < distanceOf(humidityCache, toDouble(humidity));
  }

  @VisibleForTesting
  double distanceOf(double d1, double d2) {
    return Math.abs(d1 - d2);
  }

  private void cacheMeasurements() {
    temperatureCache = toDouble(temperature);
    humidityCache = toDouble(humidity);
  }

  private double toDouble(Pointer pointer) {
    return pointer.getFloat(0);
  }

  private double applyTemperatureCorrection(double temperature) {
    return temperature + temperatureCorrection;
  }

  private double applyHumidityCorrection(double humidity) {
    return humidity + humidityCorrection;
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
