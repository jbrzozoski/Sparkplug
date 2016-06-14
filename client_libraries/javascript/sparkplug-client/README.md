Sparkplug Client
=========

A client library providing a MQTT client for MQTT device communication using the
Sparkplug Specification from Cirrus Link Solutions.  

https://s3.amazonaws.com/ignition-modules/Current/Sparkplug+Specification.pdf

The client will connect to an MQTT Server and act as an MQTT Edge of Network
(EoN) Node.  It will publish birth certificates (NBIRTH), node data messages
(NDATA), and process node command messages (NCMD) that have been sent from
another MQTT client.

The client also provides and interface for MQTT Device application code to
publish device birth certificates (DBIRTH), device data messages (DDATA), device
death certificates (DDEATH), and receive device command messages (DCMD) that
have been sent from another MQTT client.

## Installation

  npm install sparkplug-client

## Usage

### Creating and configuring a new Sparkplug client

A configuration object is required when creating a new client.  A configuration
must contain the following properties:

* serverUrl: The URL of the MQTT server.
* username: The username for the MQTT server connection.
* password: The password for the MQTT server connection.
* groupId: An ID representing a logical grouping of MQTT EoN Nodes and Devices
  into the infrastructure.
* edgeNode: An ID that uniquely identifies the MQTT EoN Node within the
  infrastructure.
* clientId: A unique ID for the MQTT client connection.

Here is a code example of creating and configuring a new client:

```javascript
var sparkplug = require('sparkplug-client'),
    config = {
        'serverUrl' : 'tcp://localhost:1883',
        'username' : 'username',
        'password' : 'password',
        'groupId' : 'Sparkplug Devices',
        'edgeNode' : 'Test Edge Node',
        'clientId' : 'JavaScriptSimpleEdgeNode'
    },
    client = sparkplug.newClient(config);
```

### Stopping the client

Once a client has been created and configured it will automatically connect to
the MQTT Server.  the client provides a function for stopping the client and
cleanly disconnecting from the MQTT Server.  Once a client has been stopped, a
new client must be created and configured in order to re-establish a connection
with the server.

Here is a code example of stopping a client:

```javascript
// Stop the sparkplug client
client.stop();
```

### Publishing messages

This client provides functions for publishing three types of messages: a device
birth certificate (DBIRTH), device data message (DDATA), device death
certificate (DDEATH)

#### Device Birth Certificate (DBIRTH)

A device birth certificate (DBIRTH) message will contain all data points,
process variables, and/or metrics for the device. The payload for this message
will consist of:

* timestamp:  A UTC timestamp represented by 64 bit integer.
* metric:  An array of metric objects. Each metric in the array must contain
  the following:
  * name:  The name of the metric.
  * value:  The value of the metric.
  * type:  The type of the metric.  The following types are supported: int,
    long, float, double, boolean, string, bytes.
  * position: An optional position object. A position contains the following
    required fields:  longitude, latitude.  A position may also contain the
    following optional fields:  altitude, precision, heading, speed, timestamp,
    satellites, status.

Here is a code example of publishing a DBIRTH message:

```javascript
var deviceId = "testDevice",
    payload = {
        "timestamp" : 1465577611580,
        "metric" : [
            {
                "name" : "my_int",
                "value" : 456,
                "type" : "int"
            },
            {
                "name" : "my_float",
                "value" : 1.23,
                "type" : "float"
            }
        ],
        "position" : {
            "latitude" : 38.83667239,
            "longitude" : -94.67176706,
            "altitude" : 319,
            "precision" : 2.0,
            "heading" : 0,
            "speed" : 0,
            "timestamp" : 1465577611580,
            "satellites" : 8,
            "status" : 3
        }
    };

// Publish device birth
client.publishDeviceBirth(deviceId, payload);
```

#### Device Data Message (DDATA)

A device data message (DDATA) will look similar to DBIRTH but is not required to
publish all metrics, but it must publish at least one.


Here is a code example of publishing a DBIRTH message:

```javascript
var deviceId = "testDevice",
    payload = {
        "timestamp" : 1465456711580,
        "metric" : [
            {
                "name" : "my_int",
                "value" : 412,
                "type" : "int"
            }
        ]
    };

// Publish device data
client.publishDeviceData(deviceId, payload);
```

#### Device Death Certificate (DDEATH)

A device death certificate (DDEATH) can be published to indicated that the
device has gone offline or has lost a connection.  It should contain only a
timestamp.


Here is a code example of publishing a DBIRTH message:

```javascript
var deviceId = "testDevice",
    payload = {
        "timestamp" : 1465456711580
    };

// Publish device death
client.publishDeviceDeath(deviceId, payload);
```

### Receiving events

The client uses an EventEmitter to emit two types of events to device
applications:  a "rebirth" event and "command" event.

#### Rebirth Event

A "rebirth" event is used to signal the device application that a DBIRTH message
is requested.  This event will be be emitted immediately after the client
initially connects with the MQTT Server or any time that the client receives a
Edge Node command (NCMD) requesting a "rebirth".

Here is a code example of handling a "rebirth" event:

```javascript
client.on('rebirth', function () {
    console.log("received 'rebirth' event");
    client.publishDeviceBirth(deviceId, getBirthPayload());
});
```

#### Command Event

A device command event is used to communicate a Device Command message (DCMD)
from another MQTT client to a device.  A 'command' event will include the device
ID and a payload containing a list of metrics (as described above).  Any metrics
included in the payload represent attempts to write a new value to the data
points or process variables that they represent.  After the device application
processes the request the device application should publish a DDATA message
containing any metrics that have changed or been updated.

Here is a code example of handling a "command" event:

```javascript
client.on('command', function (deviceId, payload) {
    console.log("received 'command' event");
    console.log("device: " + device);
    console.log("payload: " + payload);

    //
    // Process metrics and create new payload containing changed metrics
    //

    client.publishDeviceData(deviceId, newPayload);
});
```

## Release History

* 1.0.0 Initial release
* 1.0.2 Current release

## License

Copyright (c) 2016 Cirrus Link Solutions

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors: Cirrus Link Solutions
