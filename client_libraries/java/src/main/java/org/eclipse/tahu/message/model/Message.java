/*******************************************************************************
 * Copyright (c) 2014, 2018 Cirrus Link Solutions and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *  Cirrus Link Solutions
 *
 *******************************************************************************/

package org.eclipse.tahu.message.model;

/**
 * A class to represent a Message.
 */
public class Message {

	private Topic topic;
	
	private SparkplugBPayload payload;
	
	/**
	 * @param topic
	 * @param payload
	 */
	private Message(Topic topic, SparkplugBPayload payload) {
		super();
		this.topic = topic;
		this.payload = payload;
	}

	public Topic getTopic() {
		return topic;
	}

	public SparkplugBPayload getPayload() {
		return payload;
	}
	
	/**
	 * A builder for creating a {@link SparkplugBPayload} instance.
	 */
	public static class MessageBuilder {
		
		private Topic topic;
		
		private SparkplugBPayload payload;
		
		public MessageBuilder(Topic topic, SparkplugBPayload payload)  {
			this.topic = topic;
			this.payload = payload;
		}
		
		public MessageBuilder() {}
		
		public MessageBuilder topic(Topic topic) {
			this.topic = topic;
			return this;
		}
		
		public MessageBuilder payload(SparkplugBPayload payload) {
			this.payload = payload;
			return this;
		}
		
		public Message build() {
			return new Message(this.topic, this.payload);
		}
	}
}
