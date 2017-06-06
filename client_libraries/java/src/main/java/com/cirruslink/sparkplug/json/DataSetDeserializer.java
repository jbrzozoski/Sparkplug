/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2017 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.json;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cirruslink.sparkplug.SparkplugException;
import com.cirruslink.sparkplug.message.model.DataSet;
import com.cirruslink.sparkplug.message.model.DataSetDataType;
import com.cirruslink.sparkplug.message.model.Row;
import com.cirruslink.sparkplug.message.model.Value;
import com.cirruslink.sparkplug.message.model.DataSet.DataSetBuilder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * A JSON deserializer for {@link DataSet} instances.
 */
public class DataSetDeserializer extends StdDeserializer<DataSet> {
	
	private static Logger logger = LogManager.getLogger(DataSetDeserializer.class.getName());
	
	private static final String FIELD_SIZE = "numberOfColumns";
	private static final String FIELD_TYPES = "types";
	private static final String FIELD_NAMES = "columnNames";
	private static final String FIELD_ROWS = "rows";

	/**
	 * Constructor.
	 * 
	 * @param clazz
	 */
	protected DataSetDeserializer(Class<DataSet> clazz) {
		super(clazz);
	}

	@Override
	public DataSet deserialize(JsonParser parser, DeserializationContext context) 
			throws IOException, JsonProcessingException {
		JsonNode node = parser.getCodec().readTree(parser);
		long size = (Long) node.get(FIELD_SIZE).numberValue();
		DataSetBuilder builder = new DataSetBuilder(size);
		JsonNode namesNode = node.get(FIELD_NAMES);
		if (namesNode.isArray()) {
			for (JsonNode nameNode : namesNode) {
				builder.addColumnName(nameNode.textValue());
			}
		}
		JsonNode typesNode = node.get(FIELD_TYPES);
		List<DataSetDataType> typesList = new ArrayList<DataSetDataType>();
		if (typesNode.isArray()) {
			for (JsonNode typeNode : typesNode) {
				typesList.add(DataSetDataType.valueOf(typeNode.textValue()));
			}
			builder.addTypes(typesList);
		}
		JsonNode rowsNode = node.get(FIELD_ROWS);
		if (rowsNode.isArray()) {
			for (JsonNode rowNode : rowsNode) {
				List<Value<?>> values = new ArrayList<Value<?>>();
				for (int i = 0; i < size; i++) {
					JsonNode value = rowNode.get(i);
					DataSetDataType type = typesList.get(i);
					values.add(getValueFromNode(value, type));
				}
				builder.addRow(new Row(values));
			}
		}
		try {
			return builder.createDataSet();
		} catch (SparkplugException e) {
			logger.error("Error deserializing DataSet ", e);
		}
		return null;
	}
	
	/*
	 * Creates and returns a Value instance
	 */
	private Value<?> getValueFromNode(JsonNode nodeValue, DataSetDataType type) {
		switch (type) {
			case Boolean:
				return new Value<Boolean>(type, (boolean)nodeValue.asBoolean());
			case DateTime:
				return new Value<Date>(type, new Date(nodeValue.asLong()));
			case Double:
				return new Value<Double>(type, nodeValue.asDouble());
			case Float:
				return new Value<Float>(type, (float)nodeValue.asDouble());
			case Int16:
			case UInt8:
				return new Value<Byte>(type, (byte)nodeValue.asInt());
			case UInt16:
			case Int32:
				return new Value<Integer>(type, nodeValue.asInt());
			case UInt32:
			case Int64:
				return new Value<Long>(type, (long)nodeValue.asLong());
			case Text:
			case String:
				return new Value<String>(type, nodeValue.asText());
			case UInt64:
				return new Value<BigInteger>(type, BigInteger.valueOf(nodeValue.asLong()));
			case Unknown:
			default:
				return null;
		}
	}
}
