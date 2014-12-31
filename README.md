# Sensor Samplor

_A generic data sampling software for data logging and sensor networks._

![Raspberry Pi with AM2302 sensor](https://github.com/nwaldispuehl/sensor-samplor/wiki/img/raspberry_pi_with_am2302_sensor.jpg)

##### Table of Contents
* [Introduction](#introduction)
* [Manuals](#manuals)
* [Basic use cases](#basic_use_cases)
 * [A temperature data logger](#data_logger_usecase)
 * [A sensor network](#sensor_network_usecase)


<a name='introduction' />
## Introduction

This software provides a distributed communication bus based on [Hazelcast](http://hazelcast.org/) (a distributed data grid) with attached *Sensors* sending samples into the bus and *Receivers* reacting on such sensor events.
Every started instance of the **SensorSamplor** is considered a single *Node* in a cluster. Such node is configured to use a number of sensors and/or receivers.
Usually in a sensor network, there are sensing nodes which only perform sensor measurements and few nodes which do not sense anything but only receive, that is, aggregate and store the measurements sent over the bus.

The default use case is to measure temperature with a DHT22/AM2302 temperature/humidity sensor on a [Raspberry Pi](http://www.raspberrypi.org/). The software can be run on a wide variety of platforms using any thinkable sensor however. The platform needs to support [Java 7](https://www.java.com/).

<a name='manuals' />
## Manuals

More information on how to install and use the software can be found in the [wiki](https://github.com/nwaldispuehl/sensor-samplor/wiki):
* [Prepare RaspberryPi as temperature sensor node](https://github.com/nwaldispuehl/sensor-samplor/wiki/Prepare-RaspberryPi-as-temperature-sensor-node)
* [Install the SensorSamplor on Raspberry Pi](https://github.com/nwaldispuehl/sensor-samplor/wiki/Install-the-SensorSamplor-on-Raspberry-Pi)
* [Configure SensorSamplor](https://github.com/nwaldispuehl/sensor-samplor/wiki/Configure-SensorSamplor)


<a name='basic_use_cases' />
## Basic use cases

The following use cases demonstrate frequent usage patterns of the software.

<a name='data_logger_usecase' />
### Use case 1: A temperature data logger

The software can certainly be used stand-alone when we only want to log sensor data for later use. After having installed the software and the driver on a Raspberry Pi with attached sensor (see below for instructions) we want it to measure temperature (and humidity) on a regular basis and store the samples in a log file. After some weeks we will collect the Pi and analyze the logs on some other system.

#### Configuration

The following configuration items are relevant for this use case.

First, we set a speaking name for our little node. Certainly this could be left on the default value, but naming the nodes is good practice:

    sensorsamplor.sensor_platform_identifier = workshop_temperature_logger

We only put the ```temperature``` sensor into the list of active sensors. This is a string which is declared in the respective sensor plugin:

    sensorsamplor.active_sensors = temperature

Then we set the measurement timing with a [cron expression](http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger); one sample every minute:

    sensorsamplor.measurement_cron_expression = 0 * * * * ?

As receiver we set the ```logfile``` plugin:

    sensorsamplor.active_receivers = logfile

The specific sensor used here asks for an additional property which we set as follows:

    sensorsamplor.sensor.temperature.gpio_data_pin = 4

The same holds for the logfile receiver; it has to be configured with a path, so it knows where to write the files to:

    sensorsamplor.receiver.logfile.logging_directory = /var/log/sensor-samplor/

#### Output

The software then starts to measure the temperature and - on every measurement - produces an entry in a log file:

    $ ls -l /var/log/sensor-samplor

    ...
    sensor.log.temperature.2014-12-26
    sensor.log.temperature.2014-12-27
    sensor.log.temperature.2014-12-28
    sensor.log.temperature.2014-12-29
    ...

These logs then look as follows. The log file pattern is

    ```TIMESTAMP -- NODE IDENTIFIER -- SENSOR TYPE -- SAMPLE UUID -- DATA PAYLOAD```

and the contents of an actual log file thus is like this:

    $ cat /var/log/sensor-samplor/sensor.log.temperature.2014-12-27

    ...
    2014-12-27:18:06:00 +0100 -- workshop_temperature_logger -- temperature -- d38727db-cb75-4f10-b6cc-c6b12e9e7aea -- {"humidity":55.0,"temperature":20.2}
    2014-12-27:18:07:00 +0100 -- workshop_temperature_logger -- temperature -- 988b4031-30de-4697-aa6e-c4bf26fcf9f7 -- {"humidity":55.0,"temperature":20.2}
    2014-12-27:18:08:00 +0100 -- workshop_temperature_logger -- temperature -- a0c0f0c4-07ab-4fe7-b719-5186f84763f9 -- {"humidity":55.3,"temperature":20.3}
    ...

These can now be processed.

<a name='sensor_network_usecase' />
### Use case 2: A small sensor network

To have multiple nodes collecting sensor readings (also temperature in this case) we configure two Raspberry Pi computers as sensor nodes and a personal computer as some sort of sensor live ticker. We again assume the software has been installed on all three systems and temperature sensors as well as drivers were set up on the two Raspberry Pi computers. Furthermore all nodes are connected to the same computer network.

#### Configuration

The three nodes have to be configured as follows. All properties not shown here can be left on their respective default values.

Note that:
* we configure no receivers on the sensor nodes, and no sensors on the live ticker system.
* we set the measurement time interval to one minute.
* the bus configuration must be the same on all nodes which should be part of the same cluster.
* we assume that all of these nodes are in the same network subnet, and their network interface addresses are either 10.100.10.*, or 192.168.1.*.

##### Node 1

    sensorsamplor.sensor_platform_identifier = node_1
    sensorsamplor.active_sensors = temperature
    sensorsamplor.active_receivers =
    sensorsamplor.measurement_cron_expression = 0 * * * * ?

    sensorsamplor.bus.name = SensorSamplorBus
    sensorsamplor.bus.username = myUsername
    sensorsamplor.bus.password = myPassword
    sensorsamplor.bus.interfaces = 10.100.10.*, 192.168.1.*

    sensorsamplor.sensor.temperature.gpio_data_pin = 4

##### Node 2

    sensorsamplor.sensor_platform_identifier = node_2
    sensorsamplor.active_sensors = temperature
    sensorsamplor.active_receivers =
    sensorsamplor.measurement_cron_expression = 0 * * * * ?

    sensorsamplor.bus.name = SensorSamplorBus
    sensorsamplor.bus.username = myUsername
    sensorsamplor.bus.password = myPassword
    sensorsamplor.bus.interfaces = 10.100.10.*, 192.168.1.*

    sensorsamplor.sensor.temperature.gpio_data_pin = 4

##### Personal Computer

    sensorsamplor.sensor_platform_identifier = sensor_live_ticker
    sensorsamplor.active_sensors =
    sensorsamplor.active_receivers = console

    sensorsamplor.bus.name = SensorSamplorBus
    sensorsamplor.bus.username = myUsername
    sensorsamplor.bus.password = myPassword
    sensorsamplor.bus.interfaces = 10.100.10.*, 192.168.1.*

#### Output

On the personal computer, when the software is running on all nodes, the following output is written to the console:

    ...
    2014-12-27:18:45:00 +0100 -- node_1 -- temperature -- 9cc4d10d-d856-40ac-97d5-87d91b698163 -- {"humidity":25.2,"temperature":24.0}
    2014-12-27:18:45:00 +0100 -- node_2 -- temperature -- 5023d8ef-369b-49d9-8cde-f2980ca1aae8 -- {"humidity":42.6,"temperature":21.1}
    2014-12-27:18:46:00 +0100 -- node_1 -- temperature -- d90dc5ce-2e27-44f2-a575-9f7323fb5dd4 -- {"humidity":25.6,"temperature":24.2}
    2014-12-27:18:46:00 +0100 -- node_2 -- temperature -- 81cb7f69-200d-4fcb-9943-d0db578a0aa6 -- {"humidity":42.6,"temperature":21.1}
    2014-12-27:18:47:00 +0100 -- node_1 -- temperature -- 0a41690a-9180-476a-b3c4-934a6562c89f -- {"humidity":25.7,"temperature":24.1}
    2014-12-27:18:47:00 +0100 -- node_2 -- temperature -- 1c13d32c-f092-4a2c-9007-dd733066fe0e -- {"humidity":42.6,"temperature":21.2}
    ...

