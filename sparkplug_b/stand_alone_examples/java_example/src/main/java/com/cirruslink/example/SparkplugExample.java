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

import static com.cirruslink.sparkplug.message.model.MetricDataType.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.cirruslink.sparkplug.SparkplugInvalidTypeException;
import com.cirruslink.sparkplug.message.SparkplugBPayloadDecoder;
import com.cirruslink.sparkplug.message.SparkplugBPayloadEncoder;
import com.cirruslink.sparkplug.message.model.DataSet;
import com.cirruslink.sparkplug.message.model.DataSetDataType;
import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.Metric.MetricBuilder;
import com.cirruslink.sparkplug.message.model.Row;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;
import com.cirruslink.sparkplug.message.model.Value;

public class SparkplugExample implements MqttCallbackExtended {

	// HW/SW versions
	private static final String HW_VERSION = "Emulated Hardware";
	private static final String SW_VERSION = "v1.0.0";
	
	private static final String NAMESPACE = "spBv1.0";

	// Configuration
	private static final boolean USING_REAL_TLS = false;
	private String serverUrl = "tcp://localhost:1883";
	private String groupId = "Sparkplug B Devices";
	private String edgeNode = "Java Edge Node";
	private String deviceId = "Emulated Device";
	private String clientId = "javaSimpleEdgeNode";
	private String username = "admin";
	private String password = "changeme";
	private long PUBLISH_PERIOD = 60000;					// Publish period in milliseconds
	private ExecutorService executor;
	private MqttClient client;
	
	private int bdSeq = 0;
	private int seq = 0;
	
	private Object seqLock = new Object();
	
	public static void main(String[] args) {
		SparkplugExample example = new SparkplugExample();
		example.run();
	}
	
	public void run() {
		try {
			// Random generator and thread pool for outgoing published messages
			executor = Executors.newFixedThreadPool(1);
			
			// Build up DEATH payload - note DEATH payloads don't have a regular sequence number
			SparkplugBPayloadBuilder deathPayload = new SparkplugBPayloadBuilder().setTimestamp(new Date());
			deathPayload = addBdSeqNum(deathPayload);
			byte [] deathBytes = new SparkplugBPayloadEncoder().getBytes(deathPayload.createPayload());
			
			MqttConnectOptions options = new MqttConnectOptions();
			
			if(USING_REAL_TLS) {
				SocketFactory sf = SSLSocketFactory.getDefault();
				options.setSocketFactory(sf);
			}
			
			// Connect to the MQTT Server
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(30);
			options.setKeepAliveInterval(30);
			options.setUserName(username);
			options.setPassword(password.toCharArray());
			options.setWill(NAMESPACE + "/" + groupId + "/NDEATH/" + edgeNode, deathBytes, 0, false);
			client = new MqttClient(serverUrl, clientId);
			client.setTimeToWait(2000);	
			client.setCallback(this);					// short timeout on failure to connect
			client.connect(options);
			
			// Subscribe to control/command messages for both the edge of network node and the attached devices
			client.subscribe(NAMESPACE + "/" + groupId + "/NCMD/" + edgeNode + "/#", 0);
			client.subscribe(NAMESPACE + "/" + groupId + "/DCMD/" + edgeNode + "/#", 0);	
			client.subscribe(NAMESPACE + "/#", 0);	
			
			// Loop forever publishing data every PUBLISH_PERIOD
			while (true) {
				Thread.sleep(PUBLISH_PERIOD);

				if (client.isConnected()) {
					synchronized(seqLock) {
						System.out.println("Connected - publishing new data");
						// Create the payload and add some metrics
						SparkplugBPayload payload = new SparkplugBPayload(
								new Date(), 
								getRandomMetrics(), 
								getSeqNum(),
								getUUID(), 
								null);
						
						client.publish(NAMESPACE + "/" + groupId + "/DDATA/" + edgeNode + "/" + deviceId, 
								new SparkplugBPayloadEncoder().getBytes(payload), 0, false);
					}
				} else {
					System.out.println("Not connected - not publishing data");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getUUID() {
		return java.util.UUID.randomUUID().toString();
	}
	
	private List<Metric> getRandomMetrics() throws SparkplugInvalidTypeException {
		Random random = new Random();
		List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(new MetricBuilder("my_boolean", Boolean, random.nextBoolean()).createMetric());
		metrics.add(new MetricBuilder("my_byte", Int8, random.nextInt(Byte.MAX_VALUE)).createMetric());
		metrics.add(new MetricBuilder("my_short", Int16, random.nextInt(Short.MAX_VALUE)).createMetric());
		metrics.add(new MetricBuilder("my_int", Int32, random.nextInt()).createMetric());
		metrics.add(new MetricBuilder("my_long", Int64, random.nextLong()).createMetric());
		metrics.add(new MetricBuilder("my_float", Float, random.nextFloat()).createMetric());
		metrics.add(new MetricBuilder("my_double", Double, random.nextDouble()).createMetric());
		metrics.add(new MetricBuilder("my_null", String, null).createMetric());
		byte [] randomBytes = new byte[20];
		random.nextBytes(randomBytes);
		metrics.add(new MetricBuilder("my_bytes", Bytes, randomBytes).createMetric());
		metrics.add(new MetricBuilder("my_string", String, getUUID()).createMetric());
		metrics.add(new MetricBuilder("my_text", Text, "Text: " + getUUID()).createMetric());
		metrics.add(new MetricBuilder("my_dataset", DataSet, getRandomDataSet()).createMetric());
		metrics.add(new MetricBuilder("my_datetime", DateTime, new Date()).createMetric());
		
		return metrics;
	}
	
	private DataSet getRandomDataSet() {
		Random random = new Random();
		int numOfColumns = 3;
		List<String> columnNames = new ArrayList<String>();
		List<DataSetDataType> types = new ArrayList<DataSetDataType>();
		List<Row> rows = new ArrayList<Row>();
		List<Value<?>> row1List = new ArrayList<Value<?>>();
		List<Value<?>> row2List = new ArrayList<Value<?>>();
		
		columnNames.add("integers");
		columnNames.add("booleans");
		columnNames.add("strings");
		
		types.add(DataSetDataType.Int32);
		types.add(DataSetDataType.Boolean);
		types.add(DataSetDataType.String);
		
		row1List.add(new Value<Integer>(DataSetDataType.Int32, random.nextInt()));
		row1List.add(new Value<Boolean>(DataSetDataType.Boolean, random.nextBoolean()));
		row1List.add(new Value<String>(DataSetDataType.String, getUUID()));
		
		row2List.add(new Value<Integer>(DataSetDataType.Int32, random.nextInt()));
		row2List.add(new Value<Boolean>(DataSetDataType.Boolean, random.nextBoolean()));
		row2List.add(new Value<String>(DataSetDataType.String, getUUID()));
		
		rows.add(new Row(row1List));
		rows.add(new Row(row2List));
		
		return new DataSet(numOfColumns, columnNames, types, rows);
	}

	private void publishBirth() {
		try {
			synchronized(seqLock) {
				// Reset the sequence number
				seq = 0;
				
				// Create the BIRTH payload and set the position and other metrics
				SparkplugBPayload payload = new SparkplugBPayload(
						new Date(), 
						new ArrayList<Metric>(), 
						getSeqNum(),
						getUUID(), 
						null);
				
				payload.addMetric(new MetricBuilder("bdSeq", Int16, bdSeq).createMetric());		
				payload.addMetric(new MetricBuilder("Node Control/Rebirth", Boolean, false)
						.createMetric());
				
				System.out.println("Publishing Edge Node Birth");
				executor.execute(new Publisher(NAMESPACE + "/" + groupId + "/NBIRTH/" + edgeNode, payload));
	
				// Create the payload and add some metrics
				payload = new SparkplugBPayload(
						new Date(), 
						getRandomMetrics(), 
						getSeqNum(),
						getUUID(), 
						null);

				// Only do this once to set up the inputs and outputs
				payload.addMetric(new MetricBuilder("Inputs/0", Boolean, true).createMetric());
				payload.addMetric(new MetricBuilder("Inputs/1", Int32, 0).createMetric());
				payload.addMetric(new MetricBuilder("Inputs/2", Float, 1.23).createMetric());
				payload.addMetric(new MetricBuilder("Outputs/0", Boolean, true).createMetric());
				payload.addMetric(new MetricBuilder("Outputs/1", Int32, 0).createMetric());
				payload.addMetric(new MetricBuilder("Outputs/2", Double, 1.23).createMetric());
	
				// Add some properties
				payload.addMetric(new MetricBuilder("Properties/hw_version", String, HW_VERSION)
						.createMetric());
				payload.addMetric(new MetricBuilder("Properties/sw_version", String, SW_VERSION)
						.createMetric());
	
				System.out.println("Publishing Device Birth");
				executor.execute(new Publisher(NAMESPACE + "/" + groupId + "/DBIRTH/" + edgeNode + "/" + deviceId, payload));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Used to add the birth/death sequence number
	private SparkplugBPayloadBuilder addBdSeqNum(SparkplugBPayloadBuilder payload) throws Exception {
		if (payload == null) {
			payload = new SparkplugBPayloadBuilder();
		}
		if (bdSeq == 256) {
			bdSeq = 0;
		}
		payload.addMetric(new MetricBuilder("bdSeq", Int16, bdSeq).createMetric());
		bdSeq++;
		return payload;
	}
	
	// Used to add the sequence number
	private long getSeqNum() throws Exception {
		System.out.println("seq: " + seq);
		if (seq == 256) {
			seq = 0;
		}
		return seq++;
	}
	
	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		System.out.println("Connected! - publishing birth");
		publishBirth();
	}

	public void connectionLost(Throwable cause) {
		System.out.println("The MQTT Connection was lost! - will auto-reconnect");
    }

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("Message Arrived on topic " + topic);
		
		SparkplugBPayloadDecoder decoder = new SparkplugBPayloadDecoder();
		SparkplugBPayload inboundPayload = decoder.buildFromByteArray(message.getPayload());
		
		// Debug
		for (Metric metric : inboundPayload.getMetrics()) {
			System.out.println("Metric " + metric.getName() + "=" + metric.getValue());
		}
		
		String[] splitTopic = topic.split("/");
		if (splitTopic[0].equals(NAMESPACE) && 
				splitTopic[1].equals(groupId) &&
				splitTopic[2].equals("NCMD") && 
				splitTopic[3].equals(edgeNode)) {
			for (Metric metric : inboundPayload.getMetrics()) {
				if ("Node Control/Rebirth".equals(metric.getName()) && ((Boolean)metric.getValue())) {
					publishBirth();
				} else {
					System.out.println("Unknown Node Command NCMD: " + metric.getName());
				}
			}
		} else if (splitTopic[0].equals(NAMESPACE) && 
				splitTopic[1].equals(groupId) &&
				splitTopic[2].equals("DCMD") && 
				splitTopic[3].equals(edgeNode)) {
			System.out.println("Command recevied for device " + splitTopic[4]);

			// Process the incoming payload and publish any updated/modified metrics
			// Simulate the following: 
			//   Outputs/0 is tied to Inputs/0
			//   Outputs/2 is tied to Inputs/1
			//   Outputs/2 is tied to Inputs/2
			SparkplugBPayload outboundPayload = new SparkplugBPayload(
					new Date(), 
					new ArrayList<Metric>(), 
					getSeqNum(),
					getUUID(), 
					null);
			for (Metric metric : inboundPayload.getMetrics()) {
				String name = metric.getName();
				Object value = metric.getValue();
				if ("Outputs/0".equals(name)) {
					System.out.println("Outputs/0: " + value);
					outboundPayload.addMetric(new MetricBuilder("Inputs/0", Boolean, value).createMetric());
					outboundPayload.addMetric(new MetricBuilder("Outputs/0", Boolean, value).createMetric());
					System.out.println("Publishing updated value for Inputs/0 " + value);
				} else if ("Outputs/1".equals(name)) {
					System.out.println("Output1: " + value);
					outboundPayload.addMetric(new MetricBuilder("Inputs/1", Int32, value).createMetric());
					outboundPayload.addMetric(new MetricBuilder("Outputs/1", Int32, value).createMetric());
					System.out.println("Publishing updated value for Inputs/1 " + value);
				} else if ("Outputs/2".equals(name)) {
					System.out.println("Output2: " + value);
					outboundPayload.addMetric(new MetricBuilder("Inputs/2", Double, value).createMetric());
					outboundPayload.addMetric(new MetricBuilder("Outputs/2", Double, value).createMetric());
					System.out.println("Publishing updated value for Inputs/2 " + value);
				}
			}

			// Publish the message in a new thread
			executor.execute(new Publisher(NAMESPACE + "/" + groupId + "/DDATA/" + edgeNode + "/" + deviceId, outboundPayload));
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		//System.out.println("Published message: " + token);
	}
	
	private class Publisher implements Runnable {
		
		private String topic;
		private SparkplugBPayload outboundPayload;

		public Publisher(String topic, SparkplugBPayload outboundPayload) {
			this.topic = topic;
			this.outboundPayload = outboundPayload;
		}
		
		public void run() {
			try {
				outboundPayload.setTimestamp(new Date());
				SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
				client.publish(topic, encoder.getBytes(outboundPayload), 0, false);
			} catch (MqttPersistenceException e) {
				e.printStackTrace();
			} catch (MqttException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
}
