/**
 * Copyright (c) 2016 Cirrus Link Solutions
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Cirrus Link Solutions
 */

module.exports = function(RED) {
    var SparkplugClient = require('sparkplug-client');
    var deviceCache = {} // A cache of data for devices

    function SparkplugNode(config) {
        RED.nodes.createNode(this, config);
        var node = this,
            username = this.credentials.user,
            password = this.credentials.password,
            cacheEnabled = config.enablecache == "true",
            sparkPlugConfig = {
                'serverUrl' : config.broker + ":" + config.port,
                'username' : username,
                'password' : password,
                'groupId' : config.groupid,
                'edgeNode' : config.edgenode,
                'clientId' : config.clientid
            },
            hwVersion = 'Emulated Hardware',
            swVersion = 'v1.0.0',
            deviceId = 'Emulated Device',
            sparkPlugClient,
            publishPeriod = 5000,

        // Create the SparkplugClient
        sparkplugClient = SparkplugClient.newClient(sparkPlugConfig);

        // Create 'rebirth' handler
        sparkplugClient.on('rebirth', function () {
            node.log(config.edgenode + " received 'rebirth' event");
            // If device cache is enabled, send birth on behalf of device
            if (cacheEnabled) {
                console.log("!!!! " + JSON.stringify(deviceCache));
                // Loop over all devices in the device data cache
                for (var key in deviceCache) {
                    // Publish BIRTH certificate for device
                    sparkplugClient.publishDeviceBirth(key, {
                            "timestamp" : new Date().getTime(),
                            "metric" : deviceCache[key]
                        });
                }
            } else {
                node.log(config.edgenode + " sending 'rebirth' message to downstream nodes");
                node.send({
                    "topic" : "rebirth",
                    "payload" : {}
                });
            }
        });

        // Create 'command' handler
        sparkplugClient.on('command', function (deviceId, payload) {
            var timestamp = payload.timestamp,
            metric = payload.metric;
            node.log(config.edgenode + " received 'command' event, deviceId: " + deviceId);
            node.log(config.edgenode + " sending 'command' message to downstream nodes");
            node.send({
                "topic" : deviceId,
                "payload" : payload
            });

        });

        /*
         * Receive input from a device.  The topic should be of the format: <deviceId>/<messageType>, where
         * <messageType> can be one of: DDATA, DBIRTH, or DDEATH.
         *
         * The topics here are prefixed with topic version A1.0.
         *
         * The payloads should be of the following formats:
         *
         * Topic: A1.0/<deviceId>/DDATA
         * Payload: An object with a "timestamp" (required), array of ALL "metric" objects (required),
         *          and "position" (optional).
         * Example:
         * {
         *     "timestamp" : 1465577611580
         *     "metric" : [
         *         {
         *             "name" : "my_int",
         *             "value" : 456,
         *             "type" : "int"
         *         }
         *     ],
         *     "position" : {
         *         "latitude" : 38.83667239,
         *         "longitude" : -94.67176706,
         *         "altitude" : 319,
         *         "precision" : 2.0,
         *         "heading" : 0,
         *         "speed" : 0,
         *         "timestamp" : new Date().getTime(),
         *          "satellites" : 8,
         *          "status" : 3
         *      }
         * }
         *
         * Topic: A1.0/<deviceId>/DBIRTH
         * Payload: An object with a "timestamp" (required), array of ALL "metric" objects (required).
         * Example:
         * {
         *     "timestamp" : 1465577611580
         *     "metric" : [
         *         {
         *             "name" : "my_int",
         *             "value" : 456,
         *             "type" : "int"
         *         },
         *         {
         *             "name" : "my_float",
         *             "value" : 1.23,
         *             "type" : "float"
         *         }
         *     ]
         * }
         *
         * Topic: A1.0/<deviceId>/DDEATH
         * Payload: An object with a "timestamp" (required).
         * Example:
         * {
         *     "timestamp" : 1465577611580
         * }
         */
        this.on('input', function(msg) {
            var tokens = msg.topic.split("/"),
                payload = msg.payload,
                publishBirth = false,
                deviceId, messageType, cachedDevice;

            node.log(config.edgenode + " recieved input msg: " + JSON.stringify(msg));

            if (tokens.length != 3) {
                node.error(config.edgenode + " received message with invalid topic " + msg.topic + ", must be of the form <deviceId>/<msgType>");
                return;
            }

            // Parse topic to get deviceId and messageType
            deviceId = tokens[1];
            messageType = tokens[2];

            // Get cached device
            cachedDevice = deviceCache[deviceId];

            if (messageType === "DBIRTH") {
                if (cacheEnabled) {
                    if (cachedDevice !== undefined) {
                        // Update device cache
                        cachedDevice.metric = payload.metric;
                    } else {
                        deviceCache[deviceId] = payload.metric;
                    }
                }
                // Publish device birth
                sparkplugClient.publishDeviceBirth(deviceId, payload);
            } else if (messageType === "DDATA") {
                if (cacheEnabled) {
                    if (cachedDevice === undefined) {
                        node.error(config.edgenode + " received a DDATA for unknown device " + deviceId);
                        return;
                    }
                    // Update metrics in device cache
                    // Loop over incoming metrics
                    for (var i = 0; i < payload.metric.length; i++) {
                        var m = payload.metric[i],
                            found = false;
                        // Loop through existing metrics to check if the incoming metric is known
                        for (var j = 0; j < cachedDevice.metric.length; j++) {
                            if (cachedDevice.metric[j].name === m.name) {
                                // Update metric value
                                cachedDevice.metric[j].value = m.value;
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            node.warn(config.edgenode + " received a DDATA message with an unknown metric");
                            // Add new metric
                            cachedDevice.metric.push(incomingMetrics);
                        }
                    }
                }
                // Publish device data
                sparkplugClient.publishDeviceData(deviceId, payload);
            } else if (messageType === "DDEATH") {
                // Clear device cache
                delete deviceCache[deviceId];
                // Publish device data
                sparkplugClient.publishDeviceDeath(deviceId, payload);
            }
        });

        this.on('close', function() {
            // Stop the sparkplug client
            sparkplugClient.stop();
        });
    };

    // Register the sparkplug node
    RED.nodes.registerType("sparkplug", SparkplugNode, {
        credentials: {
            user: {type:"text"},
            password: {type:"password"}
        }
    });
}
