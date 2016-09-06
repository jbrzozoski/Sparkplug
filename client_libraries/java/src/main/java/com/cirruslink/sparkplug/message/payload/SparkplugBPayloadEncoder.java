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
		logger.setLevel(Level.DEBUG);
	}
	
	public byte[] getBytes(SparkplugBPayload payload) throws IOException {
		
		SparkplugBProto.Payload.Builder protoMsg = SparkplugBProto.Payload.newBuilder();
		
		// Set the timestamp
		if (payload.getTimestamp() != null) {
			logger.debug("Setting time " + payload.getTimestamp());
			protoMsg.setTimestamp(payload.getTimestamp().getTime());
		}
		
		// Set the sequence number
		logger.debug("Setting sequence number " + payload.getSeq());
		protoMsg.setSeq(payload.getSeq());
		
		// Set the UUID if defined
		if (payload.getUuid() != null) {
			logger.debug("Setting the UUID " + payload.getUuid());
			protoMsg.setUuid(payload.getUuid());
		}
		
		// Set the metrics
		for (Metric metric : payload.getMetrics()) {
			
			// build a metric
			SparkplugBProto.Payload.Metric.Builder metricBuilder = SparkplugBProto.Payload.Metric.newBuilder();
			
			try {
				// set the basic parameters
				logger.debug("Adding metric: " + metric.getName());
				metricBuilder.setName(metric.getName());
				if(metric.hasAlias()) {
					metricBuilder.setAlias(metric.getAlias());
				}
				metricBuilder.setDatatype(convertMetricDataType(metric.getDataType()));
				if (metric.getTimestamp() != null) {
					metricBuilder.setTimestamp(metric.getTimestamp().getTime());
				}
				
				// Set the value and metadata
				metricBuilder = setMetricValue(metricBuilder, metric);
				if (metric.getMetaData() != null) {
					logger.debug("Metadata is not null");
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
		metricBuilder.setDatatype(convertMetricDataType(metric.getDataType()));

		switch (metric.getDataType()) {
			case Boolean:
				metricBuilder.setBooleanValue((Boolean) metric.getValue());
				break;
			case DateTime:
				metricBuilder.setLongValue(((Date)metric.getValue()).getTime());
				break;
			case File:
				metricBuilder.setBytesValue(ByteString.copyFrom(((File) metric.getValue()).getBytes()));
				SparkplugBProto.Payload.Metric.MetaData.Builder metaDataBuilder = SparkplugBProto.Payload.Metric.MetaData.newBuilder();
				metaDataBuilder.setFileName(((File) metric.getValue()).getFileName());
				metricBuilder.setMetadata(metaDataBuilder);
				break;
			case Float4:
				metricBuilder.setFloatValue((Float) metric.getValue());
				break;
			case Float8:
				metricBuilder.setDoubleValue((Double) metric.getValue());
				break;
			case Int1:
			case Int2:
			case Int4:
				metricBuilder.setIntValue((Integer) metric.getValue());
				break;
			case Int8:
				metricBuilder.setLongValue((Long) metric.getValue());
				break;
			case String:
			case Text:
				metricBuilder.setStringValue((String) metric.getValue());
				break;
			case Bytes:
				metricBuilder.setBytesValue(ByteString.copyFrom((byte[]) metric.getValue()));
				break;
			case DataSet:
				DataSet dataSet = (DataSet) metric.getValue();
				SparkplugBProto.Payload.Metric.DataSet.Builder protoDataSetBuilder = 
						SparkplugBProto.Payload.Metric.DataSet.newBuilder();

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
						protoDataSetBuilder.addTypes(convertDataSetDataType(type));
					}
				} else {
					throw new Exception("Invalid DataSet");
				}

				// Dataset rows
				List<Row> rows = dataSet.getRows();
				if (rows != null && !rows.isEmpty()) {
					for (Row row : rows) {
						SparkplugBProto.Payload.Metric.DataSet.Row.Builder protoRowBuilder = 
								SparkplugBProto.Payload.Metric.DataSet.Row.newBuilder();
						List<Value<?>> values = row.getValues();
						if (values != null && !values.isEmpty()) {
							for (Value<?> value : values) {
								// Add the converted element
								protoRowBuilder.addElement(convertDataSetValue(value));
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
			case UdtDef:
				break;
			case UdtInst:
				break;
			case Unknown:
			default:
				logger.error("Unknown DataType: " + metric.getDataType());
				throw new Exception("Failed to encode");

		}
		
		return metricBuilder;
	}
	
	private SparkplugBProto.Payload.Metric.Builder setMetaData(SparkplugBProto.Payload.Metric.Builder metricBuilder,
			Metric metric) throws Exception {
		
		// If the builder has been built already - use it
		SparkplugBProto.Payload.Metric.MetaData.Builder metaDataBuilder;
		if (metricBuilder.getMetadataBuilder() != null) {
			metaDataBuilder = metricBuilder.getMetadataBuilder();
		} else {
			metaDataBuilder = SparkplugBProto.Payload.Metric.MetaData.newBuilder();
		}
		
		MetaData metaData = metric.getMetaData();
		if (metaData.getUnits() != null) {
			metaDataBuilder.setUnits(metaData.getUnits());
		}
		if (metaData.getContentType() != null) {
			metaDataBuilder.setContentType(metaData.getContentType());
		}
		metaDataBuilder.setSize(metaData.getSize());
		if (metaData.getAlgorithm() != null) {
			metaDataBuilder.setAlgorithm(metaData.getAlgorithm());
		}
		if (metaData.getFormat() != null) {
			metaDataBuilder.setFormat(metaData.getFormat());
		}
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
	
	private SparkplugBProto.Payload.Metric.DataSet.Value.Builder convertDataSetValue(Value<?> value) throws Exception {
		SparkplugBProto.Payload.Metric.DataSet.Value.Builder protoValueBuilder = 
				SparkplugBProto.Payload.Metric.DataSet.Value.newBuilder();

		// Set the value
		switch (value.getType()) {
			case Int1:
			case Int2:
			case Int4:
				protoValueBuilder.setIntValue((Integer) value.getValue());
				break;
			case Int8:
				protoValueBuilder.setLongValue((Long) value.getValue());
				break;
			case Float4:
				protoValueBuilder.setFloatValue((Float) value.getValue());
				break;
			case Float8:
				protoValueBuilder.setDoubleValue((Double) value.getValue());
				break;
			case String:
			case Text:
				protoValueBuilder.setStringValue((String) value.getValue());
				break;
			case Boolean:
				protoValueBuilder.setBooleanValue((Boolean) value.getValue());
				break;
			case DateTime:
				protoValueBuilder.setLongValue(((Date) value.getValue()).getTime());
				break;
			case Null:
				break;
			default:
				logger.error("Unknown DataType: " + value.getType());
				throw new Exception("Failed to convert value " + value.getType());
		}

		return protoValueBuilder;
	}
	
	private SparkplugBProto.Payload.Metric.DataSet.DataType convertDataSetDataType(DataSetDataType type) {
		switch (type) {
			case Boolean:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Boolean;
			case DateTime:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.DateTime;
			case Float4:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Float4;
			case Float8:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Float8;
			case Int1:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Int1;
			case Int2:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Int2;
			case Int4:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Int4;
			case Int8:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Int8;
			case Null:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Null;
			case String:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.String;
			case Text:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Text;
			default:
				return SparkplugBProto.Payload.Metric.DataSet.DataType.Unknown;
		}
	}
	
	private SparkplugBProto.Payload.Metric.DataType convertMetricDataType(MetricDataType type) {
		switch (type) {
			case Boolean:
				return SparkplugBProto.Payload.Metric.DataType.Boolean;
			case DateTime:
				return SparkplugBProto.Payload.Metric.DataType.DateTime;
			case Float4:
				return SparkplugBProto.Payload.Metric.DataType.Float4;
			case Float8:
				return SparkplugBProto.Payload.Metric.DataType.Float8;
			case Int1:
				return SparkplugBProto.Payload.Metric.DataType.Int1;
			case Int2:
				return SparkplugBProto.Payload.Metric.DataType.Int2;
			case Int4:
				return SparkplugBProto.Payload.Metric.DataType.Int4;
			case Int8:
				return SparkplugBProto.Payload.Metric.DataType.Int8;
			case String:
				return SparkplugBProto.Payload.Metric.DataType.String;
			case Text:
				return SparkplugBProto.Payload.Metric.DataType.Text;
			case Bytes:
				return SparkplugBProto.Payload.Metric.DataType.Bytes;
			case DataSet:
				return SparkplugBProto.Payload.Metric.DataType.Dataset;
			case File:
				return SparkplugBProto.Payload.Metric.DataType.File;
			case UdtDef:
				return SparkplugBProto.Payload.Metric.DataType.UdtDef;
			case UdtInst:
				return SparkplugBProto.Payload.Metric.DataType.UdtInst;
			default:
				return SparkplugBProto.Payload.Metric.DataType.Unknown;
		}
	}
}
