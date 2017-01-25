/**
 * Copyright (c) 2012, 2016 Cirrus Link Solutions
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Cirrus Link Solutions
 */
package com.cirruslink.example;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.cirruslink.sparkplug.message.SparkplugBPayloadDecoder;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload;
import com.cirruslink.sparkplug.message.model.Topic;
import com.cirruslink.sparkplug.util.TopicUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SparkplugListener implements MqttCallbackExtended {

	// Configuration
	private String serverUrl = "tcp://localhost:1883";
	private String clientId = "SparkplugBListenerEdgeNode";
	private String username = "admin";
	private String password = "changeme";
	private MqttClient client;
	
	public static void main(String[] args) {
		SparkplugListener listener = new SparkplugListener();
		listener.run();
	}
	
	public void run() {
		try {
			// Connect to the MQTT Server
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(30);
			options.setKeepAliveInterval(30);
			options.setUserName(username);
			options.setPassword(password.toCharArray());
			client = new MqttClient(serverUrl, clientId);
			client.setTimeToWait(5000);						// short timeout on failure to connect
			client.connect(options);
			client.setCallback(this);
			
			// Just listen to all DDATA messages on spAv1.0 topics and wait for inbound messages
			client.subscribe("spBv1.0/#", 0);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		System.out.println("Connected!");
	}

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("The MQTT Connection was lost! - will auto-reconnect");
    }

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		Topic sparkplugTopic = TopicUtil.parseTopic(topic);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		System.out.println("Message Arrived on Sparkplug topic " + sparkplugTopic.toString());
		
		SparkplugBPayloadDecoder decoder = new SparkplugBPayloadDecoder();
		SparkplugBPayload inboundPayload = decoder.buildFromByteArray(message.getPayload());
		
		// Convert the message to JSON and print to system.out
		try {
			String payloadString = mapper.writeValueAsString(inboundPayload);
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inboundPayload));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Published message: " + token);
	}
}
