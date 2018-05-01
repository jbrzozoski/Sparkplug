/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.util;

import com.cirruslink.sparkplug.SparkplugParsingException;
import com.cirruslink.sparkplug.message.model.MessageType;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload;
import com.cirruslink.sparkplug.message.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides utility methods for handling Sparkplug MQTT message topics.
 */
public class TopicUtil {

	/**
	 * Serializes a {@link Topic} instance in to a JSON string.
	 * 
	 * @param topic a {@link Topic} instance
	 * @return a JSON string
	 * @throws JsonProcessingException
	 */
	public static String toJsonString(Topic topic) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(topic);
	}

	/**
	 * Parses a Sparkplug MQTT message topic string and returns a {@link Topic} instance.
	 *
	 * @param topic a topic string
	 * @return a {@link Topic} instance
	 * @throws SparkplugParsingException if an error occurs while parsing
	 */
	public static Topic parseTopic(String topic) throws SparkplugParsingException {
		return parseTopic(topic.split("/"));
	}

	/**
	 * Parses a Sparkplug MQTT message topic string and returns a {@link Topic} instance.
	 *
	 * @param splitTopic a topic split into tokens
	 * @return a {@link Topic} instance
	 * @throws SparkplugParsingException if an error occurs while parsing
	 */
	@SuppressWarnings("incomplete-switch")
	public static Topic parseTopic(String[] splitTopic) throws SparkplugParsingException {
		MessageType type;
		String namespace, edgeNodeId, groupId;
		int length = splitTopic.length;

		if (length < 4 || length > 5) {
			throw new SparkplugParsingException("Invalid number of topic elements: " + length);
		}

		namespace = splitTopic[0];
		groupId = splitTopic[1];
		type = MessageType.parseMessageType(splitTopic[2]);
		edgeNodeId = splitTopic[3];

		if (length == 4) {
			// A node topic
			switch (type) {
				case STATE:
				case NBIRTH:
				case NCMD:
				case NDATA:
				case NDEATH:
				case NRECORD:
					return new Topic(namespace, groupId, edgeNodeId, type);
			}
		} else {
			// A device topic
			switch (type) {
				case STATE:
				case DBIRTH:
				case DCMD:
				case DDATA:
				case DDEATH:
				case DRECORD:
					return new Topic(namespace, groupId, edgeNodeId, splitTopic[4], type);
			}
		}
		throw new SparkplugParsingException("Invalid number of topic elements " + length + " for topic type " + type);
	}
}
