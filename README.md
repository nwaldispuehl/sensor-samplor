...

Work in progress...

## How to build

### Build driver

Build the DHT22/AM2302 sensor driver by calling the make file in the respective directory:

    $ cd sensorsamplor.sensor.temperature.am2302
    $ cd src/main/c
    $ make
    $ make install
    $ make clean

The driver is compiled (```libPiDht.so```) and copied into the systems library directory ```/usr/lib```.

### Build software

To the build the software, call the gradle wrapper from the projects root:

    $ ./gradlew installApp

### Run software

Then, you can run it with this command (we need the ```sudo``` to talk to the sensor):

    $ cd sensorsamplor.app/build/install/sensorsamplor.app/bin/
    $ sudo ./sensorsamplor.app

## How to deploy

...


