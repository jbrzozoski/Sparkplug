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

serverUrl = "localhost"
myGroupId = "Sparkplug B Devices"
myNodeName = "Python Edge Node"
mySubNodeName = "Emulated Device"
publishPeriod = 5000
myUsername = "admin"
myPassword = "changeme"

######################################################################
# The callback for when the client receives a CONNACK response from the server.
######################################################################
def on_connect(client, userdata, flags, rc):
    global myGroupId
    global myNodeName
    print("Connected with result code "+str(rc))

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

######################################################################

######################################################################
# Publish the Birth certificate
######################################################################
def publishBirth():
    # Create the node birth payload
    payload = sparkplug.getEdgeBirthPayload()

    # Publish the node birth certificate
    byteArray = bytearray(payload.SerializeToString())
    client.publish("spBv1.0/" + myGroupId + "/NBIRTH/" + myNodeName, byteArray, 0, False)

    # Setup the I/O
    payload = sparkplug.getDeviceBirthPayload()
    addMetric(payload, "my_Boolean", "Boolean", random.choice([True, False]))

    # Set up the propertites
    addMetric(payload, "Properties/Hardware Version", "String", "PFC_1.1")
    addMetric(payload, "Properties/Firmware Version", "String", "1.4.2")

    # Publish the initial data with the Device BIRTH certificate
    totalByteArray = bytearray(payload.SerializeToString())
    client.publish("spBv1.0/" + myGroupId + "/DBIRTH/" + myNodeName + "/" + mySubNodeName, totalByteArray, 0, False)

######################################################################


# Start of main program - Set up the MQTT client connection
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.username_pw_set(myUsername, myPassword)
#client.will_set("spBv1.0/" + myGroupId + "/NDEATH/" + myNodeName, deathByteArray, 0, False)
client.connect(serverUrl, 1883, 60)

publishBirth()

while True:
    payload = sparkplug.getDdataPayload()

#    addMetric(payload, "my_boolean", "BOOL", random.choice([True, False]))
#    addMetric(payload, "my_float", "FLOAT", random.random())
#    addMetric(payload, "my_int", "INT32", random.randint(0,100))
#    addMetric(payload, "my_long", "INT64", random.getrandbits(60))

    # Publish the data
#    byteArray = bytearray(payload.SerializeToString())
#    client.publish("spBv1.0/" + myGroupId + "/DDATA/" + myNodeName + "/" + mySubNodeName, byteArray, 0, False)

    for _ in range(50):
	time.sleep(.1)
	client.loop()
