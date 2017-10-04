package org.eclipse.paho.android.sample.model;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PublishedMessage extends Message{

    public PublishedMessage(String topic, MqttMessage message) {
        super(topic, message, MessageType.Published);
    }
}
