/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cirruslink.sparkplug.SparkplugInvalidTypeException;
import com.cirruslink.sparkplug.message.model.DataSet.DataSetBuilder;
import com.cirruslink.sparkplug.message.model.DataSetDataType;
import com.cirruslink.sparkplug.message.model.File;
import com.cirruslink.sparkplug.message.model.MetaData.MetaDataBuilder;
import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.Metric.MetricBuilder;
import com.cirruslink.sparkplug.message.model.MetricDataType;
import com.cirruslink.sparkplug.message.model.Parameter;
import com.cirruslink.sparkplug.message.model.ParameterDataType;
import com.cirruslink.sparkplug.message.model.PropertyDataType;
import com.cirruslink.sparkplug.message.model.PropertySet;
import com.cirruslink.sparkplug.message.model.PropertySet.PropertySetBuilder;
import com.cirruslink.sparkplug.message.model.PropertyValue;
import com.cirruslink.sparkplug.message.model.Row;
import com.cirruslink.sparkplug.message.model.Row.RowBuilder;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload;
import com.cirruslink.sparkplug.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;
import com.cirruslink.sparkplug.message.model.Template;
import com.cirruslink.sparkplug.message.model.Template.TemplateBuilder;
import com.cirruslink.sparkplug.message.model.Value;
import com.cirruslink.sparkplug.protobuf.SparkplugBProto;

/**
 * A {@link PayloadDecode} implementation for decoding Sparkplug B payloads.
 */
public class SparkplugBPayloadDecoder implements PayloadDecoder <SparkplugBPayload> {
	
	private static Logger logger = LogManager.getLogger(SparkplugBPayloadDecoder.class.getName());

	public SparkplugBPayloadDecoder() {
		super();
	}
	
	public SparkplugBPayload buildFromByteArray(byte[] bytes) throws Exception {
		SparkplugBProto.Payload protoPayload = SparkplugBProto.Payload.parseFrom(bytes);
		SparkplugBPayloadBuilder builder = new SparkplugBPayloadBuilder(protoPayload.getSeq());
		
		// Set the timestamp
		if (protoPayload.hasTimestamp()) {
			logger.trace("Setting time " + new Date(protoPayload.getTimestamp()));
			builder.setTimestamp(new Date(protoPayload.getTimestamp()));
		}
		
		// Set the sequence number
		if (protoPayload.hasSeq()) {
			logger.trace("Setting sequence number " + protoPayload.getSeq());
			builder.setSeq(protoPayload.getSeq());
		}
		
		// Set the Metrics
		for (SparkplugBProto.Payload.Metric protoMetric : protoPayload.getMetricsList()) {
			builder.addMetric(convertMetric(protoMetric));
		}
		
		// Set the body
		if (protoPayload.hasBody()) {
			logger.trace("Setting the body " + new String(protoPayload.getBody().toByteArray()));
			builder.setBody(protoPayload.getBody().toByteArray());
		}
		
		return builder.createPayload();
	}
	
	private Metric convertMetric(SparkplugBProto.Payload.Metric protoMetric) throws Exception {
		// Convert the dataType
		MetricDataType dataType = MetricDataType.fromInteger((protoMetric.getDatatype()));
		
		// Build and return the Metric
		return new MetricBuilder(protoMetric.getName(), dataType, getMetricValue(protoMetric))
				.isHistorical(protoMetric.getIsHistorical())
				.isTransient(protoMetric.getIsTransient())
				.timestamp(new Date(protoMetric.getTimestamp()))
				.alias(protoMetric.hasAlias() 
						? protoMetric.getAlias() 
						: null)
				.metaData(protoMetric.hasMetadata() 
						? new MetaDataBuilder()
								.contentType(protoMetric.getMetadata().getContentType())
								.size(protoMetric.getMetadata().getSize())
								.seq(protoMetric.getMetadata().getSeq())
								.fileName(protoMetric.getMetadata().getFileName())
								.fileType(protoMetric.getMetadata().getFileType())
								.md5(protoMetric.getMetadata().getMd5())
								.description(protoMetric.getMetadata().getDescription())
								.createMetaData()
						: null)
				.propertySet(protoMetric.hasProperties()
						? new PropertySetBuilder()
								.addProperties(convertProperties(protoMetric.getProperties()))
								.createPropertySet()
						: null)
				.createMetric();
	}
	
	private Map<String, PropertyValue> convertProperties(SparkplugBProto.Payload.PropertySet decodedPropSet) 
			throws SparkplugInvalidTypeException, Exception {
		Map<String, PropertyValue> map = new HashMap<String, PropertyValue>();
		List<String> keys = decodedPropSet.getKeysList();
		List<SparkplugBProto.Payload.PropertyValue> values = decodedPropSet.getValuesList();
		for (int i = 0; i < keys.size(); i++) {
			SparkplugBProto.Payload.PropertyValue value = values.get(i);
			map.put(keys.get(i), new PropertyValue(PropertyDataType.fromInteger(value.getType()), 
					getPropertyValue(value)));
		}
		return map;
	}
	
	private Object getPropertyValue(SparkplugBProto.Payload.PropertyValue value) throws Exception {
		PropertyDataType type = PropertyDataType.fromInteger(value.getType());
		if (value.getIsNull()) {
			return null;
		}
		switch (type) {
			case Boolean:
				return value.getBooleanValue();
			case DateTime:
				return new Date(value.getLongValue());
			case Float:
				return value.getFloatValue();
			case Double:
				return value.getDoubleValue();
			case Int8:
				return (byte) value.getIntValue();
			case Int16:
			case UInt8:
				return (short) value.getIntValue();
			case Int32:
			case UInt16:
				return value.getIntValue();
			case UInt32:
			case Int64:
				return value.getLongValue();
			case UInt64:
				return BigInteger.valueOf(value.getLongValue());
			case String:
			case Text:
				return value.getStringValue();
			case PropertySet:
				return new PropertySetBuilder()
						.addProperties(convertProperties(value.getPropertysetValue()))
						.createPropertySet();
			case PropertySetList:
				List<PropertySet> propertySetList = new ArrayList<PropertySet>();
				List<SparkplugBProto.Payload.PropertySet> list = value.getPropertysetsValue().getPropertysetList();
				for (SparkplugBProto.Payload.PropertySet decodedPropSet : list) {
					propertySetList.add(new PropertySetBuilder()
							.addProperties(convertProperties(decodedPropSet))
							.createPropertySet());
				}
				return propertySetList;
			case Unknown:
			default:
				throw new Exception("Failed to decode: Unknown Property Data Type " + type);
		}
	}
	
	private Object getMetricValue(SparkplugBProto.Payload.Metric protoMetric) throws Exception {
		// Check if the null flag has been set indicating that the value is null
		if (protoMetric.getIsNull()) {
			return null;
		}
		// Otherwise convert the value based on the type
		int metricType = protoMetric.getDatatype();
		switch (MetricDataType.fromInteger(metricType)) {
			case Boolean:
				return protoMetric.getBooleanValue();
			case DateTime:
				return new Date(protoMetric.getLongValue());
			case File:
				String filename = protoMetric.getMetadata().getFileName();
				byte [] fileBytes = protoMetric.getBytesValue().toByteArray();
				return new File(filename, fileBytes);
			case Float:
				return protoMetric.getFloatValue();
			case Double:
				return protoMetric.getDoubleValue();
			case Int8:
				return (byte) protoMetric.getIntValue();
			case Int16:
			case UInt8:
				return (short) protoMetric.getIntValue();
			case Int32:
			case UInt16:
				return protoMetric.getIntValue();
			case UInt32:
			case Int64:
				return protoMetric.getLongValue();
			case UInt64:
				return BigInteger.valueOf(protoMetric.getLongValue());
			case String:
			case Text:
			case UUID:
				return protoMetric.getStringValue();
			case Bytes:
				return protoMetric.getBytesValue().toByteArray();
			case DataSet:
				SparkplugBProto.Payload.DataSet protoDataSet = protoMetric.getDatasetValue();
				// Build the and create the DataSet
				return new DataSetBuilder(protoDataSet.getNumOfColumns())
						.addColumnNames(protoDataSet.getColumnsList())
						.addTypes(convertDataSetDataTypes(protoDataSet.getTypesList()))
						.addRows(convertDataSetRows(protoDataSet.getRowsList(), protoDataSet.getTypesList()))
						.createDataSet();	
			case Template:
				SparkplugBProto.Payload.Template protoTemplate = protoMetric.getTemplateValue();
				List<Metric> metrics = new ArrayList<Metric>();
				List<Parameter> parameters = new ArrayList<Parameter>();
				
				for (SparkplugBProto.Payload.Template.Parameter protoParameter : protoTemplate.getParametersList()) {
					String name = protoParameter.getName();
					logger.trace("Parameter name: " + name);
					ParameterDataType type = ParameterDataType.fromInteger(protoParameter.getType());
					logger.trace("Parameter type: " + type);
					Object value = getParameterValue(protoParameter);
					logger.trace("Setting template parameter name: " + name + ", type: " + type + ", value: " + value + ", valueType" + value.getClass());
					
					parameters.add(new Parameter(name, type, value));
				}
				
				for (SparkplugBProto.Payload.Metric protoTemplateMetric : protoTemplate.getMetricsList()) {
					Metric templateMetric = convertMetric(protoTemplateMetric);
					logger.trace("Setting template parameter name: " + templateMetric.getName() + ", type: " 
							+ templateMetric.getDataType() + ", value: " + templateMetric.getValue());
					metrics.add(templateMetric);
				}
				
				Template template = new TemplateBuilder()
						.version(protoTemplate.getVersion())
						.templateRef(protoTemplate.getTemplateRef())
						.definition(protoTemplate.getIsDefinition())
						.addMetrics(metrics)
						.addParameters(parameters)
						.createTemplate();
				
				logger.trace("Setting template - name: " + protoMetric.getName() 
						+ ", version: " + template.getVersion() 
						+ ", ref: " + template.getTemplateRef() 
						+ ", isDef: " + template.isDefinition() 
						+ ", metrics: " + metrics.size() 
						+ ", params: " + parameters.size());
				
				return template;
			case Unknown:
			default:
				throw new Exception("Failed to decode: Unknown Metric DataType " + metricType);

		}
	}
	
	private Collection<Row> convertDataSetRows(List<SparkplugBProto.Payload.DataSet.Row> protoRows, List<Integer> protoTypes) 
			throws Exception {
		Collection<Row> rows = new ArrayList<Row>();
		if (protoRows != null) {
			for (SparkplugBProto.Payload.DataSet.Row protoRow : protoRows) {
				List<SparkplugBProto.Payload.DataSet.DataSetValue> protoValues = protoRow.getElementsList();
				List<Value<?>> values = new ArrayList<Value<?>>();
				for (int index = 0; index < protoRow.getElementsCount(); index++) {
					values.add(convertDataSetValue(protoTypes.get(index), protoValues.get(index)));
				}
				// Add the values to the row and the row to the rows
				rows.add(new RowBuilder().addValues(values).createRow());
			}
		}
		return rows;
	}

	private Collection<DataSetDataType> convertDataSetDataTypes(List<Integer> protoTypes) {
		List<DataSetDataType> types = new ArrayList<DataSetDataType>();
		// Build up a List of column types
		for (int type : protoTypes) {
			types.add(DataSetDataType.fromInteger(type));
		}
		return types;
	}
	
	private Object getParameterValue(SparkplugBProto.Payload.Template.Parameter protoParameter) throws Exception {
		// Otherwise convert the value based on the type
		int type = protoParameter.getType();
		switch (MetricDataType.fromInteger(type)) {
			case Boolean:
				return protoParameter.getBooleanValue();
			case DateTime:
				return new Date(protoParameter.getLongValue());
			case Float:
				return protoParameter.getFloatValue();
			case Double:
				return protoParameter.getDoubleValue();
			case Int8:
				return (byte) protoParameter.getIntValue();
			case Int16:
			case UInt8:
				return (short) protoParameter.getIntValue();
			case Int32:
			case UInt16:
				return protoParameter.getIntValue();
			case UInt32:
			case Int64:
				return protoParameter.getLongValue();
			case UInt64:
				return BigInteger.valueOf(protoParameter.getLongValue());
			case String:
			case Text:
				return protoParameter.getStringValue();
			case Unknown:
			default:
				throw new Exception("Failed to decode: Unknown Parameter Type " + type);
		}
	}
	
	private Value<?> convertDataSetValue(int protoType, SparkplugBProto.Payload.DataSet.DataSetValue protoValue) 
			throws Exception {
		
		DataSetDataType type = DataSetDataType.fromInteger(protoType);
		switch (type) {
			case Boolean:
				return new Value<Boolean>(type, protoValue.getBooleanValue());
			case DateTime:
				return new Value<Date>(type, new Date(protoValue.getLongValue()));
			case Float:
				return new Value<Float>(type, protoValue.getFloatValue());
			case Double:
				return new Value<Double>(type, protoValue.getDoubleValue());
			case Int8:
				return new Value<Byte>(type, (byte)protoValue.getIntValue());
			case UInt8:
			case Int16:
				return new Value<Short>(type, (short)protoValue.getIntValue());
			case UInt16:
			case Int32:
				return new Value<Integer>(type, protoValue.getIntValue());
			case UInt32:
			case Int64:
				return new Value<Long>(type, protoValue.getLongValue());
			case UInt64:
				return new Value<BigInteger>(type, BigInteger.valueOf(protoValue.getLongValue()));
			case String:
				return new Value<String>(type, protoValue.getStringValue());
			case Text:
				return new Value<String>(type, protoValue.getStringValue());
			case Unknown:
			default:
				logger.error("Unknown DataType: " + protoType);
				throw new Exception("Failed to decode");	
		}
	}
}
