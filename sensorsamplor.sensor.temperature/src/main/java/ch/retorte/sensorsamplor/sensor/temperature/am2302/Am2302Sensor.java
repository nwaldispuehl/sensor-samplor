package ch.retorte.sensorsamplor.sensor.temperature.am2302;

import ch.retorte.sensorsamplor.sensor.Sensor;
import ch.retorte.sensorsamplor.sensor.SensorException;
import ch.retorte.sensorsamplor.sensor.temperature.TemperatureHumiditySample;
import ch.retorte.sensorsamplor.sensor.temperature.TemperatureHumiditySensor;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import static ch.retorte.sensorsamplor.sensor.temperature.am2302.Am2302SensorStatusCode.*;

/**
 * Implementation of the temperature/humidity sensor which uses a native c library for communicating with the DHT22/AM2302 sensor. Is not thread safe, that is, must be used only by one instance.
 */
public class Am2302Sensor implements Sensor {

  public static final String TEMPERATURE_SENSOR_TYPE = "temperature";

  private static final String PI_DHT_LIBRARY_NAME = "PiDht";
  private static final int SENSOR_TYPE = 22;
  private static final int FLOAT_SIZE = 16;
  private static final int RETRIES_IN_ERROR_CASE = 3;

  private String platformIdentifier;
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
  }

  @Override
  public TemperatureHumiditySample measure() throws SensorException {
    measureWithRetries();

    if (measurementFailed()) {
      throw new SensorException(platformIdentifier, TEMPERATURE_SENSOR_TYPE, messageOfStatus(returnCode));
    }

    return new TemperatureHumiditySample(platformIdentifier, toDouble(temperature), toDouble(humidity));
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
    }
  }

  private boolean hasTriesLeft() {
    return triesSoFar < RETRIES_IN_ERROR_CASE;
  }

  private boolean measurementFailed() {
    return returnCode != DHT_SUCCESS.code();
  }

  private double toDouble(Pointer pointer) {
    return pointer.getFloat(0);
  }
}
