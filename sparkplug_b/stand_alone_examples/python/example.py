#!/usr/local/bin/python
######################################################################
# Copyright (c) 2012, 2016 Cirrus Link Solutions
#
#  All rights reserved. This program and the accompanying materials
#  are made available under the terms of the Eclipse Public License v1.0
#  which accompanies this distribution, and is available at
#  http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#   Cirrus Link Solutions
######################################################################

import sys
sys.path.insert(0, "../../../client_libraries/python/")
#print(sys.path)

import paho.mqtt.client as mqtt
import sparkplug_b as sparkplug
import time
import random

from sparkplug_b import *

# Application Variables
serverUrl = "localhost"
myGroupId = "Sparkplug B Devices"
myNodeName = "Python Edge Node 1"
myDeviceName = "Emulated Device"
publishPeriod = 5000
myUsername = "admin"
myPassword = "changeme"

######################################################################
# The callback for when the client receives a CONNACK response from the server.
######################################################################
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    global myGroupId
    global myNodeName

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("spBv1.0/" + myGroupId + "/NCMD/" + myNodeName + "/#")
    client.subscribe("spBv1.0/" + myGroupId + "/DCMD/" + myNodeName + "/#")
######################################################################

######################################################################
# The callback for when a PUBLISH message is received from the server.
######################################################################
def on_message(client, userdata, msg):
    print("Message arrived: " + msg.topic)
    tokens = msg.topic.split("/")

    if tokens[0] == "spBv1.0" and tokens[1] == myGroupId and tokens[2] == "NCMD" and tokens[3] == myNodeName:
        inboundPayload = sparkplug_b_pb2.Payload()
        inboundPayload.ParseFromString(msg.payload)
        for metric in inboundPayload.metrics:
            if metric.name == "Node Control/Next Server":
                # Todo
		print "'Next Server' NCMD Not implemented yet"
            elif metric.name == "Node Control/Rebirth":
                publishBirth()
            elif metric.name == "Node Control/Reboot":
                publishBirth()
            else:
                print "Unknown command..."
    else:
        print "Unknown command..."

    print "done publishing"
######################################################################

######################################################################
# Publish the BIRTH certificates
######################################################################
def publishBirth():
    publishNodeBirth()
    publishDeviceBirth()
######################################################################

######################################################################
# Publish the NBIRTH certificate
######################################################################
def publishNodeBirth():
    print "Publishing Node Birth"

    # Create the node birth payload
    payload = sparkplug.getNodeBirthPayload()

    # Set up the Node Controls
    addMetric(payload, "Node Control/Next Server", MetricDataType.Boolean, False)
    addMetric(payload, "Node Control/Rebirth", MetricDataType.Boolean, False)
    addMetric(payload, "Node Control/Reboot", MetricDataType.Boolean, False)

    # Add some regular node metrics
    addMetric(payload, "Node Metric0", MetricDataType.String, "hello node")
    addMetric(payload, "Node Metric1", MetricDataType.Boolean, True)

    # Create a DataSet (012 - 345) two rows with Int8, Int16, and Int32 contents and headers Int8s, Int16s, Int32s and add it to the payload
    columns = ["Int8s", "Int16s", "Int32s"]
    types = [DataSetDataType.Int8, DataSetDataType.Int16, DataSetDataType.Int32]
    dataset = initDatasetMetric(payload, "DataSet", columns, types)
    row = dataset.rows.add()
    element = row.elements.add();
    element.int_value = 0
    element = row.elements.add();
    element.int_value = 1
    element = row.elements.add();
    element.int_value = 2
    row = dataset.rows.add()
    element = row.elements.add();
    element.int_value = 3
    element = row.elements.add();
    element.int_value = 4
    element = row.elements.add();
    element.int_value = 5

    # Add a metric with a custom property
    metric = addMetric(payload, "Node Metric2", MetricDataType.Int16, 13)
    metric.properties.keys.extend(["engUnit"])
    propertyValue = metric.properties.values.add()
    propertyValue.type = ParameterDataType.String
    propertyValue.string_value = "MyCustomUnits"

    # Publish the node birth certificate
    byteArray = bytearray(payload.SerializeToString())
    client.publish("spBv1.0/" + myGroupId + "/NBIRTH/" + myNodeName, byteArray, 0, False)
######################################################################

######################################################################
# Publish the DBIRTH certificate
######################################################################
def publishDeviceBirth():
    print "Publishing Device Birth"

    # Get the payload
    payload = sparkplug.getDeviceBirthPayload()

    # Set up the propertites
    addMetric(payload, "Properties/Hardware Version", MetricDataType.String, "PFC_1.1")
    addMetric(payload, "Properties/Firmware Version", MetricDataType.String, "1.4.2")

    # Add some simple metrics
    addMetric(payload, "my_boolean", MetricDataType.Boolean, random.choice([True, False]))
    addMetric(payload, "my_float", MetricDataType.Float, random.random())
    addMetric(payload, "my_int", MetricDataType.Int32, random.randint(0,100))
    addMetric(payload, "my_long", MetricDataType.Int64, random.getrandbits(60))

    # Publish the initial data with the Device BIRTH certificate
    totalByteArray = bytearray(payload.SerializeToString())
    client.publish("spBv1.0/" + myGroupId + "/DBIRTH/" + myNodeName + "/" + myDeviceName, totalByteArray, 0, False)
######################################################################

######################################################################
# Main Application
######################################################################
print "Starting main application"

# Create the node death payload
deathPayload = sparkplug.getNodeDeathPayload()

# Start of main program - Set up the MQTT client connection
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.username_pw_set(myUsername, myPassword)
deathByteArray = bytearray(deathPayload.SerializeToString())
client.will_set("spBv1.0/" + myGroupId + "/NDEATH/" + myNodeName, deathByteArray, 0, False)
client.connect(serverUrl, 1883, 60)

# Short delay to allow connect callback to occur
time.sleep(.1)
client.loop()

# Publish the birth certificates
publishBirth()

while True:
    payload = sparkplug.getDdataPayload()

    addMetric(payload, "my_boolean", MetricDataType.Boolean, random.choice([True, False]))
    addMetric(payload, "my_float", MetricDataType.Float, random.random())
    addMetric(payload, "my_int", MetricDataType.Int32, random.randint(0,100))
    addMetric(payload, "my_long", MetricDataType.Int64, random.getrandbits(60))

    # Publish a message periodically data
    byteArray = bytearray(payload.SerializeToString())
    client.publish("spBv1.0/" + myGroupId + "/DDATA/" + myNodeName + "/" + myDeviceName, byteArray, 0, False)

    # Sit and wait for inbound or outbound events
    for _ in range(50):
        time.sleep(.1)
        client.loop()
######################################################################
