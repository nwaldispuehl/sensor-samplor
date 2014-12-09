package ch.retorte.pitempstation.sensor.temperaturehumidity.am2302;

import ch.retorte.pitempstation.sensor.SensorException;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySample;
import ch.retorte.pitempstation.sensor.temperaturehumidity.TemperatureHumiditySensor;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * Created by nw on 07.12.14.
 */
public class Am2302Sensor implements TemperatureHumiditySensor {

  private static final String PI_DHT_LIBRARY_NAME = "PiDht";
  private static final int SENSOR_TYPE = 22;
  private static final int FLOAT_SIZE = 16;

  private final int pin;
  private final Pointer humidityReference = new Memory(FLOAT_SIZE);
  private final Pointer temperatureReference = new Memory(FLOAT_SIZE);

  private Am2302SensorLibrary sensorLibrary;

  private interface Am2302SensorLibrary extends Library {
    public int pi_dht_read(int sensor, int pin, Pointer humidity, Pointer temperature);
  }

  public Am2302Sensor(int pin) {
    this.pin = pin;
    this.sensorLibrary = (Am2302SensorLibrary) Native.loadLibrary(PI_DHT_LIBRARY_NAME, Am2302SensorLibrary.class);
  }

  @Override
  public TemperatureHumiditySample measure() throws SensorException {
    sensorLibrary.pi_dht_read(SENSOR_TYPE, pin, humidityReference, temperatureReference);
    float temperature = temperatureReference.getFloat(0);
    float humidity = humidityReference.getFloat(0);

    if (temperature == 0 && humidity == 0) {
      throw new SensorException("Problems with reading the sensor...");
    }

    return new TemperatureHumiditySample((double) temperature, (double) humidity);
  }
}
