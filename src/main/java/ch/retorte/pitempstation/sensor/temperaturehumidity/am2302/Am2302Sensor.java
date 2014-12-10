package ch.retorte.pitempstation.sensor.temperaturehumidity.am2302;

import ch.retorte.pitempstation.sensor.SensorException;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySample;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensor;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import static ch.retorte.pitempstation.sensor.temperaturehumidity.am2302.Am2302SensorStatusCode.DHT_SUCCESS;
import static ch.retorte.pitempstation.sensor.temperaturehumidity.am2302.Am2302SensorStatusCode.UNKNOWN;
import static ch.retorte.pitempstation.sensor.temperaturehumidity.am2302.Am2302SensorStatusCode.valueOfStatus;

/**
 * Implementation of the temperature/humidity sensor which uses a native c library for communicating with the DHT22/AM2302 sensor.
 */
public class Am2302Sensor implements TemperatureHumiditySensor {

  private static final String PI_DHT_LIBRARY_NAME = "PiDht";
  private static final int SENSOR_TYPE = 22;
  private static final int FLOAT_SIZE = 16;

  private final int pin;
  private final Pointer humidityReference = new Memory(FLOAT_SIZE);
  private final Pointer temperatureReference = new Memory(FLOAT_SIZE);

  private final Am2302SensorLibrary sensorLibrary;

  private interface Am2302SensorLibrary extends Library {
    public int pi_dht_read(int sensor, int pin, Pointer humidity, Pointer temperature);
  }

  public Am2302Sensor(int pin) {
    this.pin = pin;
    this.sensorLibrary = (Am2302SensorLibrary) Native.loadLibrary(PI_DHT_LIBRARY_NAME, Am2302SensorLibrary.class);
  }

  @Override
  public TemperatureHumiditySample measure() throws SensorException {
    int returnCode = sensorLibrary.pi_dht_read(SENSOR_TYPE, pin, humidityReference, temperatureReference);

    if (isNoSuccess(returnCode)) {
      throw new SensorException(getMessageFor(returnCode));
    }

    return new TemperatureHumiditySample((double) temperatureReference.getFloat(0), (double) humidityReference.getFloat(0));
  }

  private boolean isNoSuccess(int returnCode) {
    return returnCode != DHT_SUCCESS.code();
  }

  private String getMessageFor(int returnCode) {
    Am2302SensorStatusCode statusCode = valueOfStatus(returnCode);
    if (statusCode != null) {
      statusCode.message();
    }
    return UNKNOWN.message();
  }
}
