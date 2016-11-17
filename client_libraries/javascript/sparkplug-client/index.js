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

var mqtt = require('mqtt'),
    kurapayload = require('./lib/kurapayload.js'),
    sparkplugbpayload = require('./lib/sparkplugbpayload.js'),
    events = require('events'),
    util = require("util");

var getRequiredProperty = function(config, propName) {
    if (config[propName] !== undefined) {
        return config[propName];
    }
    throw new Error("Missing required configuration property '" + propName + "'");
};

var getProperty = function(config, propName, defaultValue) {
    if (config[propName] !== undefined) {
        return config[propName];
    } else {
        return defaultValue;
    }
};

/*
 * Sparkplug Client
 */
function SparkplugClient(config) {
    var versionA = "spAv1.0",
        versionB = "spBv1.0",
        serverUrl = getRequiredProperty(config, "serverUrl"),
        username = getRequiredProperty(config, "username"),
        password = getRequiredProperty(config, "password"),
        groupId = getRequiredProperty(config, "groupId"),
        edgeNode = getRequiredProperty(config, "edgeNode"),
        clientId = getRequiredProperty(config, "clientId"),
        publishDeath = getProperty(config, "publishDeath", false),
        version = getProperty(config, "version", versionB),
        bdSeq = 0,
        seq = 0,
        devices = [],
        client = null,
        connecting = false,
        connected = false,
        type_int32 = 7,
        type_boolean = 11,
        type_string = 12,

    // Increments a sequence number
    incrementSeqNum = function() {
        if (seq == 256) {
            seq = 0;
        }
        return seq++;
    },

    encodePayload = function(payload) {
        if (version === versionA) {
            return kurapayload.generateKuraPayload(payload);
        } else {
            return sparkplugbpayload.generateSparkplugPayload(payload);
        }
    },

    decodePayload = function(payload) {
        if (version === versionA) {
            return kurapayload.parseKuraPayload(payload);
        } else {
            return sparkplugbpayload.parseSparkplugPayload(payload);
        }
    },

    addSeqNumber = function(payload) {
        if (version === versionA) {
            payload.metric = payload.metric !== undefined
                ? payload.metric
                : [];
            payload.metric.push({ "name" : "seq", "value" : incrementSeqNum(), "type" : "int" });
        } else {
            payload.seq = incrementSeqNum();
        }   
    },

    // Get DEATH payload
    getDeathPayload = function() {
        var payload = {
                "timestamp" : new Date().getTime()
            },
            metric = [ {
                "name" : "bdSeq", 
                "value" : bdSeq, 
                "type" : "int"
            } ];
        if (version === versionA) {
            paylaod.metric = metric;
        } else {
            payload.metrics = metric;
        }
        return payload;
    },

    // Get BIRTH payload for the edge node
    getEdgeBirthPayload = function() {
        if (version === "spAv1") {
            return {
                "timestamp" : new Date().getTime(),
                "metric" : [
                    { "name" : "bdSeq", "value" : bdSeq, "type" : "int" },
                    { "name" : "seq", "value" : incrementSeqNum(), "type" : "int" },
                    { "name" : "Node Control/Rebirth", "value" : false, "type" : "boolean" }
                ]
            };
        } else {
            return {
                "timestamp" : new Date().getTime(),
                "seq" : incrementSeqNum(),
                "metric" : [
                    { 
                        "name" : "bdSeq",
                        "type" : type_int32, 
                        "value" : bdSeq
                    },
                    {
                        "name" : "Node Control/Rebirth",
                        "type" : type_boolean,
                        "value" : false
                    }
                ]
            };
        }
    },

    // Publishes BIRTH certificates for the edge node
    publishNBirth = function(client) {
        var payload, topic;
        // Reset sequence number
        seq = 0;

        // Publish BIRTH certificate for edge node
        console.log("Publishing Edge Node Birth");
        payload = getEdgeBirthPayload();
        topic = version + "/" + groupId + "/NBIRTH/" + edgeNode;
        client.publish(topic, encodePayload(payload));
        messageAlert("published", topic, payload);
    },

    // Publishes DEATH certificates for the edge node
    publishNDeath = function(client) {
        var payload, topic;

        // Publish DEATH certificate for edge node
        console.log("Publishing Edge Node Death");
        payload = getDeathPayload();
        topic = version + "/" + groupId + "/NDEATH/" + edgeNode;
        client.publish(topic, encodePayload(payload));
        messageAlert("published", topic, payload);
    },

    // Logs a message alert to the console
    messageAlert = function(alert, topic, payload) {
        console.log("Message " + alert);
        console.log(" topic: " + topic);
        console.log(" payload: " + JSON.stringify(payload));
    };

    events.EventEmitter.call(this);

    this.publishDeviceData = function(deviceId, payload) {
        // Add seq number
        addSeqNumber(payload);
        // Publish
        console.log("Publishing DDATA for device " + deviceId);
        topic = version + "/" + groupId + "/DDATA/" + edgeNode + "/" + deviceId;
        client.publish(topic, encodePayload(payload));
        messageAlert("published", topic, payload);
    };

    this.publishDeviceBirth = function(deviceId, payload) {
        // Add seq number
        addSeqNumber(payload);
        // Publish
        console.log("Publishing DBIRTH for device " + deviceId);
        topic = version + "/" + groupId + "/DBIRTH/" + edgeNode + "/" + deviceId;
        client.publish(topic, encodePayload(payload));
        messageAlert("published", topic, payload);
    };

    this.publishDeviceDeath = function(deviceId, payload) {
        // Add seq number
        addSeqNumber(payload);
        // Publish
        console.log("Publishing DDEATH for device " + deviceId);
        topic = version + "/" + groupId + "/DDEATH/" + edgeNode + "/" + deviceId;
        client.publish(topic, encodePayload(payload));
        messageAlert("published", topic, payload);
    };

    this.stop = function() {
        console.log("publishDeath: " + publishDeath);
        if (publishDeath) {
            // Publish the DEATH certificate
            publishNDeath(client);
        }
        client.end();
    };

    // Configures and connects the client
    return (function(sparkplugClient) {
        var deathPayload = getDeathPayload(),
            // Client connection options
            clientOptions = {
                "clientId" : clientId,
                "clean" : true,
                "keepalive" : 30,
                "connectionTimeout" : 30,
                "username" : username,
                "password" : password,
                "will" : {
                    "topic" : version + "/" + groupId + "/NDEATH/" + edgeNode,
                    "payload" : encodePayload(deathPayload),
                    "qos" : 0,
                    "retain" : false
                }
            };

        // Connect to the MQTT server
        sparkplugClient.connecting = true;
        console.log("Attempting to connect: " + serverUrl);
        console.log("              options: " + JSON.stringify(clientOptions));
        client = mqtt.connect(serverUrl, clientOptions);
        console.log("Finished attempting to connect");

        /*
         * 'connect' handler
         */
        client.on('connect', function () {
            console.log("Client has connected");
            sparkplugClient.connecting = false;
            sparkplugClient.connected = true;
            sparkplugClient.emit("connect");

            // Subscribe to control/command messages for both the edge node and the attached devices
            console.log("Subscribing to control/command messages for both the edge node and the attached devices");
            client.subscribe(version + "/" + groupId + "/NCMD/" + edgeNode + "/#", { "qos" : 0 });
            client.subscribe(version + "/" + groupId + "/DCMD/" + edgeNode + "/#", { "qos" : 0 });

            // Publish BIRTH certificates
            publishNBirth(client);
            // Emit the "rebirth" event to notify devices to send a birth
            console.log("Emmitting 'Rebirth' event");
            sparkplugClient.emit("rebirth");
        });

        /*
         * 'error' handler
         */
        client.on('error', function(error) {
            if (sparkplugClient.connecting) {
                sparkplugClient.emit("error", error);
                client.end();
            }
        });

        /*
         * 'close' handler
         */
        client.on('close', function() {
            if (sparkplugClient.connected) {
                sparkplugClient.connected = false;
                sparkplugClient.emit("close");
            }
        });

        /*
         * 'reconnect' handler
         */
        client.on("reconnect", function() {
            sparkplugClient.emit("reconnect");
        });

        /*
         * 'message' handler
         */
        client.on('message', function (topic, message) {
            var payload = decodePayload(message),
                timestamp = payload.timestamp,
                splitTopic,
                metrics;

            messageAlert("arrived", topic, payload);

            // Split the topic up into tokens
            splitTopic = topic.split("/");
            if (splitTopic[0] === version
                    && splitTopic[1] === groupId
                    && splitTopic[2] === "NCMD"
                    && splitTopic[3] === edgeNode) {
                // Loop over the metrics looking for commands
                metrics = (version === versionA) 
                        ? payload.metric
                        : paylaod.metrics;
                if (metrics !== undefined && metrics !== null) {
                    for (var i = 0; i < metrics.length; i++) {
                        var metric = metrics[i];
                        if (metric.name == "Node Control/Rebirth" && metric.value) {
                            console.log("Received 'Rebirth' command");
                            // Publish BIRTH certificate for the edge node
                            publishNBirth(client);
                            // Emit the "rebirth" event
                            console.log("Emmitting 'rebirth' event");
                            sparkplugClient.emit("rebirth");
                        }
                    }
                }
            } else if (splitTopic[0] === version
                    && splitTopic[1] === groupId
                    && splitTopic[2] === "DCMD"
                    && splitTopic[3] === edgeNode) {
                console.log("Command recevied for device " + splitTopic[4]);
                // Emit the "command" event for the given deviceId
                sparkplugClient.emit("command", splitTopic[4], payload);
            }
        });

        return sparkplugClient;
    }(this));
};

util.inherits(SparkplugClient, events.EventEmitter);

exports.newClient = function(config) {
    return new SparkplugClient(config);
};