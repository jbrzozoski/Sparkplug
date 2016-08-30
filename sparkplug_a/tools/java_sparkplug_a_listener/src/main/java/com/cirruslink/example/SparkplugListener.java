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

import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.kura.core.cloud.CloudPayloadProtoBufDecoderImpl;
import org.eclipse.kura.message.KuraPayload;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SparkplugListener implements MqttCallbackExtended {

	// Configuration
	private String serverUrl = "tcp://localhost:1883";
	private String clientId = "javaSimpleEdgeNode";
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
			client.subscribe("spAv1.0/+/DDATA/#", 0);
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
		System.out.println("Message Arrived on topic " + topic);
		
		CloudPayloadProtoBufDecoderImpl decoder = new CloudPayloadProtoBufDecoderImpl(message.getPayload());
		KuraPayload inboundPayload = decoder.buildFromByteArray();
		
		// Display everything
		System.out.println("\tTimestamp: " + inboundPayload.getTimestamp());

		if(inboundPayload.getPosition() != null) {
			System.out.println("\tPosition Altitude: " + inboundPayload.getPosition().getAltitude());
			System.out.println("\tPosition Heading: " + inboundPayload.getPosition().getHeading());
			System.out.println("\tPosition Latitude: " + inboundPayload.getPosition().getLatitude());
			System.out.println("\tPosition Longitude: " + inboundPayload.getPosition().getLongitude());
			System.out.println("\tPosition Precision: " + inboundPayload.getPosition().getPrecision());
			System.out.println("\tPosition Satellites: " + inboundPayload.getPosition().getSatellites());
			System.out.println("\tPosition Speed: " + inboundPayload.getPosition().getSpeed());
			System.out.println("\tPosition Status: " + inboundPayload.getPosition().getStatus());
			System.out.println("\tPosition Timestamp: " + inboundPayload.getPosition().getTimestamp());
		} else {
			System.out.println("\tPosition:  null");
		}
		
		Iterator<Entry<String, Object>> it = inboundPayload.metrics().entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Object> entry = it.next();
			System.out.println("\tMetric: " + entry.getKey() + "=" + entry.getValue());
		}
		
		// Alternate display in JSON
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		String jsonInString = mapper.writeValueAsString(inboundPayload);
		System.out.println("\tJSON format: " + jsonInString);
		
		System.out.println();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Published message: " + token);
	}
}
