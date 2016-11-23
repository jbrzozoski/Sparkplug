#!/usr/bin/python
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
sys.path.insert(0, "client_lib")

import paho.mqtt.client as mqtt
import pibrella
import sparkplug_b as sparkplug
import time
import random
import subprocess

from sparkplug_b import *
from threading import Lock

serverUrl = "192.168.1.150"
myGroupId = "Sparkplug B Devices"
myNodeName = "Python Raspberry Pi"
mySubNodeName = "Pibrella"
myUsername = "admin"
myPassword = "changeme"
lock = Lock()

######################################################################
# Button press event handler
######################################################################
def button_changed(pin):
    outboundPayload = sparkplug.getDdataPayload()
    buttonValue = pin.read()
    if buttonValue == 1:
        print("You pressed the button!")
    else:
        print("You released the button!")
    addMetric(outboundPayload, "button", "Boolean", buttonValue);
    byteArray = bytearray(outboundPayload.SerializeToString())
    client.publish("spBv1.0/" + myGroupId + "/DDATA/" + myNodeName + "/" + mySubNodeName, byteArray, 0, False)

######################################################################
# Input change event handler
######################################################################
def input_a_changed(pin):
    input_changed("Inputs/a", pin)
def input_b_changed(pin):
    input_changed("Inputs/b", pin)
def input_c_changed(pin):
    input_changed("Inputs/c", pin)
def input_d_changed(pin):
    input_changed("Inputs/d", pin)
def input_changed(name, pin):
    lock.acquire()
    try:
        # Lock the block around the callback handler to prevent inproper access based on debounce
        outboundPayload = sparkplug.getDdataPayload()
        addMetric(outboundPayload, name, "Boolean", pin.read());
        byteArray = bytearray(outboundPayload.SerializeToString())
        client.publish("spBv1.0/" + myGroupId + "/DDATA/" + myNodeName + "/" + mySubNodeName, byteArray, 0, False)
    finally:
        lock.release()
######################################################################

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
    tokens = msg.topic.split("/")

    if tokens[0] == "spBv1.0" and tokens[1] == myGroupId and tokens[2] == "DCMD" and tokens[3] == myNodeName:
        inboundPayload = sparkplug_b_pb2.Payload()
        inboundPayload.ParseFromString(msg.payload)
        outboundPayload = sparkplug.getDdataPayload()

        for metric in inboundPayload.metric:
            print "Tag Name: " + metric.name
            if metric.name == "Outputs/e":
                pibrella.output.e.write(metric.boolean_value)
                addMetric(outboundPayload, "Outputs/e", "Boolean", pibrella.output.e.read())
            elif metric.name == "Outputs/f":
                pibrella.output.f.write(metric.boolean_value)
                addMetric(outboundPayload, "Outputs/f", "Boolean", pibrella.output.f.read())
            elif metric.name == "Outputs/g":
                pibrella.output.g.write(metric.boolean_value)
                addMetric(outboundPayload, "Outputs/g", "Boolean", pibrella.output.g.read())
            elif metric.name == "Outputs/h":
                pibrella.output.h.write(metric.boolean_value)
                addMetric(outboundPayload, "Outputs/h", "Boolean", pibrella.output.h.read())
            elif metric.name == "Outputs/LEDs/green":
                if metric.boolean_value:
                    pibrella.light.green.on()
                else:
                    pibrella.light.green.off()
                addMetric(outboundPayload, "Outputs/LEDs/green", "Boolean", pibrella.light.green.read())
            elif metric.name == "Outputs/LEDs/red":
                if metric.boolean_value:
                    pibrella.light.red.on()
                else:
                    pibrella.light.red.off()
                addMetric(outboundPayload, "Outputs/LEDs/red", "Boolean", pibrella.light.red.read())
            elif metric.name == "Outputs/LEDs/yellow":
                if metric.boolean_value:
                    pibrella.light.yellow.on()
                else:
                    pibrella.light.yellow.off()
                addMetric(outboundPayload, "Outputs/LEDs/yellow", "Boolean", pibrella.light.yellow.read())
            elif metric.name == "buzzer_fail":
                pibrella.buzzer.fail()
            elif metric.name == "buzzer_success":
                pibrella.buzzer.success()

        byteArray = bytearray(outboundPayload.SerializeToString())
        client.publish("spBv1.0/" + myGroupId + "/DDATA/" + myNodeName + "/" + mySubNodeName, byteArray, 0, False)
    elif tokens[0] == "spBv1.0" and tokens[1] == myGroupId and tokens[2] == "NCMD" and tokens[3] == myNodeName:
        inboundPayload = sparkplug_b_pb2.Payload()
        inboundPayload.ParseFromString(msg.payload)
        for metric in inboundPayload.metric:
            if metric.name == "Node Control/Rebirth":
                publishBirth()
    else:
        print "Unknown command..."

    print "done publishing"
######################################################################

######################################################################
# Publish the Birth certificate
######################################################################
def publishBirth():
    print("Publishing Birth")

    # Create the node birth payload
    payload = sparkplug.getNodeBirthPayload()

    # Set up the device Parameters
    p = subprocess.Popen('uname -a', shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    for line in p.stdout.readlines():
        unameOutput = line,
    retVal = p.wait()
    p = subprocess.Popen('cat /proc/cpuinfo | grep Hardware', shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    for line in p.stdout.readlines():
        hardwareOutput = line,
    retVal = p.wait()
    p = subprocess.Popen('cat /proc/cpuinfo | grep Revision', shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    for line in p.stdout.readlines():
        revisionOutput = line,
    retVal = p.wait()
    p = subprocess.Popen('cat /proc/cpuinfo | grep Serial', shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    for line in p.stdout.readlines():
        serialOutput = line,
    retVal = p.wait()
    addMetric(payload, "Parameters/sw_version", "String", ''.join(unameOutput))
    addMetric(payload, "Parameters/hw_version", "String", ''.join(hardwareOutput))
    addMetric(payload, "Parameters/hw_revision", "String", ''.join(revisionOutput))
    addMetric(payload, "Parameters/hw_serial", "String", ''.join(serialOutput))

    # Publish the node birth certificate
    byteArray = bytearray(payload.SerializeToString())
    client.publish("spBv1.0/" + myGroupId + "/NBIRTH/" + myNodeName, byteArray, 0, False)

    # Set up the input metrics
    payload = sparkplug.getDeviceBirthPayload()

    addMetric(payload, "Inputs/a", "Boolean", pibrella.input.a.read())
    addMetric(payload, "Inputs/b", "Boolean", pibrella.input.b.read())
    addMetric(payload, "Inputs/c", "Boolean", pibrella.input.c.read())
    addMetric(payload, "Inputs/d", "Boolean", pibrella.input.d.read())

    # Set up the output states on first run so Ignition and MQTT Engine are aware of them
    addMetric(payload, "Outputs/e", "Boolean", pibrella.output.e.read())
    addMetric(payload, "Outputs/f", "Boolean", pibrella.output.f.read())
    addMetric(payload, "Outputs/g", "Boolean", pibrella.output.g.read())
    addMetric(payload, "Outputs/h", "Boolean", pibrella.output.h.read())
    addMetric(payload, "Outputs/LEDs/green", "Boolean", pibrella.light.green.read())
    addMetric(payload, "Outputs/LEDs/red", "Boolean", pibrella.light.red.read())
    addMetric(payload, "Outputs/LEDs/yellow", "Boolean", pibrella.light.yellow.read())
    addMetric(payload, "button", "Boolean", pibrella.button.read())
    addMetric(payload, "buzzer_fail", "Boolean", 0)
    addMetric(payload, "buzzer_success", "Boolean", 0)

    # Publish the initial data with the Device BIRTH certificate
    totalByteArray = bytearray(payload.SerializeToString())
    client.publish("spBv1.0/" + myGroupId + "/DBIRTH/" + myNodeName + "/" + mySubNodeName, totalByteArray, 0, False)
######################################################################

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

publishBirth()

# Set up the button press event handler
pibrella.button.changed(button_changed)
pibrella.input.a.changed(input_a_changed)
pibrella.input.b.changed(input_b_changed)
pibrella.input.c.changed(input_c_changed)
pibrella.input.d.changed(input_d_changed)

# Sit and wait for inbound or outbound events
while True:
    time.sleep(.1)
    client.loop()

