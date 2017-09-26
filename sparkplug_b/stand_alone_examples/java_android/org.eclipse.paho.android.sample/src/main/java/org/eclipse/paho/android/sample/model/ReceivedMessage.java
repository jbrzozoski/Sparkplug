package org.eclipse.paho.android.sample.model;

import org.eclipse.paho.client.mqttv3.MqttMessage;


public class ReceivedMessage extends Message {

    public ReceivedMessage(String topic, MqttMessage message) {
        super(topic, message, MessageType.Received);
    }
}
