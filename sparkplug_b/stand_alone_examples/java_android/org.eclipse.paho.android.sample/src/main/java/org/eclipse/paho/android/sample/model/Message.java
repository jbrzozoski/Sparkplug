package org.eclipse.paho.android.sample.model;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;

public class Message {

    private final String topic;
    private final MqttMessage message;
    private final Date timestamp;
    private final MessageType type;

    public Message(String topic, MqttMessage message, MessageType type) {
        this.topic = topic;
        this.message = message;
        this.timestamp = new Date();
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public MqttMessage getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public MessageType getMessageType() {
        return type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "topic='" + topic + '\'' +
                ", message=" + message +
                ", timestamp=" + timestamp +
                ", type=" + type +
                '}';
    }
}
