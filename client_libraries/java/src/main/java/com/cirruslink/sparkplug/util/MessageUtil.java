/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2017 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.util;

import com.cirruslink.sparkplug.message.model.Message;
import com.cirruslink.sparkplug.message.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities for Sparkplug Message handling.
 */
public class MessageUtil {
	
	/**
	 * Serializes a {@link Topic} instance in to a JSON string.
	 * 
	 * @param topic a {@link Topic} instance
	 * @return a JSON string
	 * @throws JsonProcessingException
	 */
	public static String toJsonString(Message message) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(message);
	}

}
