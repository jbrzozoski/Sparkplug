/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2017 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.eclipse.tahu.json;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.internal.websocket.Base64;
import org.eclipse.tahu.message.model.File;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Serializes a {@link File} instance.
 */
public class FileSerializer extends StdSerializer<File> {

	/**
	 * Constructor.
	 */
	protected FileSerializer() {
		super(File.class);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param clazz class.
	 */
	protected FileSerializer(Class<File> clazz) {
		super(clazz);
	}

	@Override
	public void serialize(File value, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.writeString(Base64.encodeBytes(value.getBytes()));
	}

}
