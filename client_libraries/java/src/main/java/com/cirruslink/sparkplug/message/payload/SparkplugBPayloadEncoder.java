/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.payload;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cirruslink.sparkplug.message.model.DataSet;
import com.cirruslink.sparkplug.message.model.DataSetDataType;
import com.cirruslink.sparkplug.message.model.File;
import com.cirruslink.sparkplug.message.model.MetaData;
import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.MetricDataType;
import com.cirruslink.sparkplug.message.model.Row;
import com.cirruslink.sparkplug.message.model.Value;
import com.cirruslink.sparkplug.protobuf.SparkplugBProto;
import com.google.protobuf.ByteString;

public class SparkplugBPayloadEncoder implements PayloadEncoder <SparkplugBPayload> {
	
	private static Logger logger = LogManager.getLogger(SparkplugBPayloadEncoder.class.getName());
	
	public SparkplugBPayloadEncoder() {
		super();
	}
	
	public byte[] getBytes(SparkplugBPayload payload) throws IOException {
		
		SparkplugBProto.Payload.Builder protoMsg = SparkplugBProto.Payload.newBuilder();
		
		// Set the timestamp
		if (payload.getTimestamp() != null) {
			logger.trace("Setting time " + payload.getTimestamp());
			protoMsg.setTimestamp(payload.getTimestamp().getTime());
		}
		
		// Set the sequence number
		logger.trace("Setting sequence number " + payload.getSeq());
		protoMsg.setSeq(payload.getSeq());
		
		// Set the UUID if defined
		if (payload.getUuid() != null) {
			logger.trace("Setting the UUID " + payload.getUuid());
			protoMsg.setUuid(payload.getUuid());
		}
		
		// Set the metrics
		for (Metric metric : payload.getMetrics()) {
			
			// build a metric
			SparkplugBProto.Payload.Metric.Builder metricBuilder = SparkplugBProto.Payload.Metric.newBuilder();
			
			try {
				// set the basic parameters
				logger.debug("Adding metric: " + metric.getName());
				logger.trace("    data type: " + metric.getDataType());
				metricBuilder.setName(metric.getName());
				if(metric.hasAlias()) {
					metricBuilder.setAlias(metric.getAlias());
				}
				metricBuilder.setDatatype(metric.getDataType().toIntValue());
				if (metric.getTimestamp() != null) {
					metricBuilder.setTimestamp(metric.getTimestamp().getTime());
				}
				
				// Set the value and metadata
				metricBuilder = setMetricValue(metricBuilder, metric);
				if (metric.getMetaData() != null) {
					logger.trace("Metadata is not null");
					metricBuilder = setMetaData(metricBuilder, metric);
				}
				
				protoMsg.addMetric(metricBuilder);
			} catch(Exception e) {
				logger.error("Failed to add metric: " + metric.getName());
				throw new RuntimeException(e);
			}
		}
		

		// Set the body
		if (payload.getBody() != null) {
			logger.debug("Setting the body " + new String(payload.getBody()));
			protoMsg.setBody(ByteString.copyFrom(payload.getBody()));
		}

		return protoMsg.build().toByteArray();
	}

	private SparkplugBProto.Payload.Metric.Builder setMetricValue(SparkplugBProto.Payload.Metric.Builder metricBuilder,
			Metric metric) throws Exception {

		// Set the datatype
		metricBuilder.setDatatype(metric.getDataType().toIntValue());

		if (metric.getValue() == null) {
			metricBuilder.setIsNull(true);
		} else {
			switch (metric.getDataType()) {
				case Boolean:
					metricBuilder.setBooleanValue(toBoolean(metric.getValue()));
					break;
				case DateTime:
					metricBuilder.setLongValue(((Date)metric.getValue()).getTime());
					break;
				case File:
					metricBuilder.setBytesValue(ByteString.copyFrom(((File) metric.getValue()).getBytes()));
					SparkplugBProto.Payload.MetaData.Builder metaDataBuilder = 
							SparkplugBProto.Payload.MetaData.newBuilder();
					metaDataBuilder.setFileName(((File) metric.getValue()).getFileName());
					metricBuilder.setMetadata(metaDataBuilder);
					break;
				case Float:
					metricBuilder.setFloatValue((Float) metric.getValue());
					break;
				case Double:
					metricBuilder.setDoubleValue((Double) metric.getValue());
					break;
				case Int8:
				case Int16:
				case Int32:
				case UInt8:
				case UInt16:
				case UInt32:
					metricBuilder.setIntValue((Integer) metric.getValue());
					break;
				case Int64:
				case UInt64:
					metricBuilder.setLongValue((Long) metric.getValue());
					break;
				case String:
				case Text:
				case UUID:
					metricBuilder.setStringValue((String) metric.getValue());
					break;
				case Bytes:
					metricBuilder.setBytesValue(ByteString.copyFrom((byte[]) metric.getValue()));
					break;
				case DataSet:
					DataSet dataSet = (DataSet) metric.getValue();
					SparkplugBProto.Payload.DataSet.Builder protoDataSetBuilder = 
							SparkplugBProto.Payload.DataSet.newBuilder();

					protoDataSetBuilder.setNumOfColumns(dataSet.getNumOfColumns());

					// Column names
					List<String> columnNames = dataSet.getColumnNames();
					if (columnNames != null && !columnNames.isEmpty()) {
						for (String name : columnNames) {
							// Add the column name
							protoDataSetBuilder.addColumns(name);
						}
					} else {
						throw new Exception("Invalid DataSet");
					}

					// Column types
					List<DataSetDataType> columnTypes = dataSet.getTypes();
					if (columnTypes != null && !columnTypes.isEmpty()) {
						for (DataSetDataType type : columnTypes) {
							// Add the column type
							protoDataSetBuilder.addTypes(type.toIntValue());
						}
					} else {
						throw new Exception("Invalid DataSet");
					}

					// Dataset rows
					List<Row> rows = dataSet.getRows();
					if (rows != null && !rows.isEmpty()) {
						for (Row row : rows) {
							SparkplugBProto.Payload.DataSet.Row.Builder protoRowBuilder = 
									SparkplugBProto.Payload.DataSet.Row.newBuilder();
							List<Value<?>> values = row.getValues();
							if (values != null && !values.isEmpty()) {
								for (Value<?> value : values) {
									// Add the converted element
									protoRowBuilder.addElements(convertDataSetValue(value));
								}

								logger.debug("Adding row");
								protoDataSetBuilder.addRows(protoRowBuilder);
							} else {
								throw new Exception("Invalid DataSet");
							}
						}
					}

					// Finally add the dataset
					logger.debug("Adding the dataset");
					metricBuilder.setDatasetValue(protoDataSetBuilder);

					break;
				case Template:
					break;
				case Unknown:
				default:
					logger.error("Unknown DataType: " + metric.getDataType());
					throw new Exception("Failed to encode");

			}
		}
		return metricBuilder;
	}
	
	private SparkplugBProto.Payload.Metric.Builder setMetaData(SparkplugBProto.Payload.Metric.Builder metricBuilder,
			Metric metric) throws Exception {
		
		// If the builder has been built already - use it
		SparkplugBProto.Payload.MetaData.Builder metaDataBuilder;
		if (metricBuilder.getMetadataBuilder() != null) {
			metaDataBuilder = metricBuilder.getMetadataBuilder();
		} else {
			metaDataBuilder = SparkplugBProto.Payload.MetaData.newBuilder();
		}
		
		MetaData metaData = metric.getMetaData();
		if (metaData.getContentType() != null) {
			metaDataBuilder.setContentType(metaData.getContentType());
		}
		metaDataBuilder.setSize(metaData.getSize());
		metaDataBuilder.setSeq(metaData.getSeq());
		if (metaData.getFileName() != null) {
			metaDataBuilder.setFileName(metaData.getFileName());
		}
		if (metaData.getFileType() != null) {
			metaDataBuilder.setFileType(metaData.getFileType());
		}
		if (metaData.getMd5() != null) {
			metaDataBuilder.setMd5(metaData.getMd5());
		}
		if (metaData.getDescription() != null) {
			metaDataBuilder.setDescription(metaData.getDescription());
		}
		metricBuilder.setMetadata(metaDataBuilder);
		
		return metricBuilder;
	}
	
	private SparkplugBProto.Payload.DataSet.DataSetValue.Builder convertDataSetValue(Value<?> value) throws Exception {
		SparkplugBProto.Payload.DataSet.DataSetValue.Builder protoValueBuilder = 
				SparkplugBProto.Payload.DataSet.DataSetValue.newBuilder();

		// Set the value
		DataSetDataType type = value.getType();
		switch (type) {
			case Int8:
			case Int16:
			case Int32:
			case UInt8:
			case UInt16:
			case UInt32:
				protoValueBuilder.setIntValue((Integer) value.getValue());
				break;
			case Int64:
			case UInt64:
				protoValueBuilder.setLongValue((Long) value.getValue());
				break;
			case Float:
				protoValueBuilder.setFloatValue((Float) value.getValue());
				break;
			case Double:
				protoValueBuilder.setDoubleValue((Double) value.getValue());
				break;
			case String:
			case Text:
				protoValueBuilder.setStringValue((String) value.getValue());
				break;
			case Boolean:
				protoValueBuilder.setBooleanValue(toBoolean(value.getValue()));
				break;
			case DateTime:
				protoValueBuilder.setLongValue(((Date) value.getValue()).getTime());
				break;
			default:
				logger.error("Unknown DataType: " + value.getType());
				throw new Exception("Failed to convert value " + value.getType());
		}

		return protoValueBuilder;
	}
	
	private Boolean toBoolean(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Integer) {
			return ((Integer)value).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Long) {
			return ((Long)value).longValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Float) {
			return ((Float)value).floatValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Double) {
			return ((Double)value).doubleValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Short) {
			return ((Short)value).shortValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof Byte) {
			return ((Byte)value).byteValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else if (value instanceof String) {
			return Boolean.parseBoolean(value.toString());
		}
		return (Boolean)value;
	}
}
