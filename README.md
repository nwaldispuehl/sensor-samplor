...


## How to build

### Build driver

Build the DHT22/AM2302 sensor driver by calling the make file in the respective directory:

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

    $ cd build/install/pi-temp-station/bin
    $ sudo pi-temp-station

## How to deploy

...

