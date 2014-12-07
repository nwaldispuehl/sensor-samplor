...


## How to build

### Build driver

Build the DHT22/ sensor driver by calling the make file in the respective directory:

    $ cd src/main/c
    $ make
    $ make install
    $ make clean

The driver is compiled (```libPiDht.so```) and copied into the systems library directory ```/usr/local/lib```.

### Build software

To the build the software, call the gradle wrapper from the projects root:

    $ ./gradlew installApp

...


