/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2017 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.json;

import java.io.IOException;
import java.util.Base64;

import com.cirruslink.sparkplug.message.model.File;
import com.cirruslink.sparkplug.message.model.MetaData;
import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.MetricDataType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * A custom JSON deserializer for {@link Metric} instances.
 */
public class MetricDeserializer extends StdDeserializer<Metric> implements ResolvableDeserializer {

	private final JsonDeserializer<?> defaultDeserializer;

	/**
	 * Constructor.
	 */
	protected MetricDeserializer(JsonDeserializer<?> defaultDeserializer) {
		super(Metric.class);
		this.defaultDeserializer = defaultDeserializer;
	}

	@Override
	public Metric deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		Metric metric = (Metric) defaultDeserializer.deserialize(parser, ctxt);
		System.out.println(metric);
		
		// Check if the data type is a File
		if (metric.getDataType().equals(MetricDataType.File)) {
			// Perform the custom logic for File types by building up the File object.
			MetaData metaData = metric.getMetaData();
			String fileName = metaData == null ? null : metaData.getFileName();
			File file = new File(fileName, Base64.getDecoder().decode((String)metric.getValue()));
			metric.setValue(file);
		}
		return metric;
	}

	@Override
	public void resolve(DeserializationContext ctxt) throws JsonMappingException {
		((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
	}

}
