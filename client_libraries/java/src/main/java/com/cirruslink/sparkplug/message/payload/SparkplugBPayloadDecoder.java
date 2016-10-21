/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.payload;

import java.util.ArrayList;
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

public class SparkplugBPayloadDecoder implements PayloadDecoder <SparkplugBPayload> {
	
	private static Logger logger = LogManager.getLogger(SparkplugBPayloadDecoder.class.getName());

	public SparkplugBPayloadDecoder() {
		super();
	}
	
	public SparkplugBPayload buildFromByteArray(byte[] bytes) throws Exception {
		SparkplugBProto.Payload protoPayload = SparkplugBProto.Payload.parseFrom(bytes);
		SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
		
		// Set the timestamp
		if (protoPayload.hasTimestamp()) {
			logger.debug("Setting time " + new Date(protoPayload.getTimestamp()));
			sparkplugBPayload.setTimestamp(new Date(protoPayload.getTimestamp()));
		}
		
		// Set the sequence number
		if (protoPayload.hasSeq()) {
			logger.debug("Setting sequence number " + protoPayload.getSeq());
			sparkplugBPayload.setSeq(protoPayload.getSeq());
		}
		
		// Set the Metrics
		for (int i = 0; i < protoPayload.getMetricCount(); i++) {
		
			// Get the Metric from protobuf
			SparkplugBProto.Payload.Metric protoMetric = protoPayload.getMetric(i);

			// Create a new Metric object
			Metric metric = new Metric();
			
			// Get the value
			metric.setValue(getValue(protoMetric));
			
			// Set the other tag data
			metric.setName(protoMetric.getName());
			if(protoMetric.hasAlias()) {
				metric.setAlias(protoMetric.getAlias());
			}
			metric.setTimestamp(new Date(protoMetric.getTimestamp()));
			metric.setDataType(convertMetricType(protoMetric.getDatatype()));
			metric.setHistorical(protoMetric.getHistorical());
			
			// Set the metadata
			if (protoMetric.hasMetadata()) {
				logger.debug("Metadata is not null");
				SparkplugBProto.Payload.Metric.MetaData protoMetaData = protoMetric.getMetadata();
				MetaData metaData = new MetaData();
				metaData.setContentType(protoMetaData.getContentType());
				metaData.setSize(protoMetaData.getSize());
				metaData.setSeq(protoMetaData.getSeq());
				metaData.setFileName(protoMetaData.getFileName());
				metaData.setFileType(protoMetaData.getFileType());
				metaData.setMd5(protoMetaData.getMd5());
				metaData.setDescription(protoMetaData.getDescription());			
				metric.setMetaData(metaData);
			}
			
			sparkplugBPayload.addMetric(metric);
		}
		
		// Set the body
		if (protoPayload.hasBody()) {
			logger.debug("Setting the body " + new String(protoPayload.getBody().toByteArray()));
			sparkplugBPayload.setBody(protoPayload.getBody().toByteArray());
		}
		
		return sparkplugBPayload;
	}
	
	private Object getValue(SparkplugBProto.Payload.Metric protoMetric) throws Exception {
		// Check if the null flag has been set indicating that the value is null
		if (protoMetric.getNull()) {
			return null;
		}
		// Otherwise convert the value based on the type
		switch (protoMetric.getDatatype()) {
			case Boolean:
				return protoMetric.getBooleanValue();
			case DateTime:
				return new Date(protoMetric.getLongValue());
			case File:
				String filename = protoMetric.getMetadata().getFileName();
				byte [] fileBytes = protoMetric.getBytesValue().toByteArray();
				return new File(filename, fileBytes);
			case Float4:
				return protoMetric.getFloatValue();
			case Float8:
				return protoMetric.getDoubleValue();
			case Int1:
			case Int2:
			case Int4:
				return protoMetric.getIntValue();
			case Int8:
				return protoMetric.getLongValue();
			case String:
			case Text:
				return protoMetric.getStringValue();
			case Bytes:
				return protoMetric.getBytesValue().toByteArray();
			case Dataset:
				DataSet dataSet = new DataSet();
				SparkplugBProto.Payload.Metric.DataSet protoDataSet = protoMetric.getDatasetValue();
				List<String> protoColumns = protoDataSet.getColumnsList();
				List<SparkplugBProto.Payload.Metric.DataSet.DataType> protoTypes;
				List<SparkplugBProto.Payload.Metric.DataSet.Row> protoRows = protoDataSet.getRowsList();
				long numOfColumns = protoDataSet.getNumOfColumns();
				dataSet.setNumOfColumns(numOfColumns);

				// Set the column names and types
				if (protoDataSet.getColumnsCount() > 0) {
					// Build up a List of column names
					protoColumns = protoDataSet.getColumnsList();
					List<String> columnNames = new ArrayList<String>();
					for (String name : protoColumns) {
						columnNames.add(name);
					}

					// Set the columns names
					dataSet.setColumnNames(columnNames);
				} else {
					throw new Exception("Failed to decode: DataSet must have column names");
				}

				if (protoDataSet.getTypesCount() > 0) {
					// Build up a List of column types
					protoTypes = protoDataSet.getTypesList();
					List<DataSetDataType> types = new ArrayList<DataSetDataType>();
					for (SparkplugBProto.Payload.Metric.DataSet.DataType type : protoTypes) {
						types.add(convertValueType(type));
					}

					// Set the column types
					dataSet.setTypes(types);
				} else {
					throw new Exception("Failed to decode: DataSet must have types");
				}

				// Set the rows
				if (protoDataSet.getRowsCount() > 0) {
					List<Row> rows = new ArrayList<Row>();
					for (SparkplugBProto.Payload.Metric.DataSet.Row protoRow : protoRows) {
						if (protoRow.getElementCount() > 0) {
							List<SparkplugBProto.Payload.Metric.DataSet.Value> protoValues = protoRow.getElementList();
							List<Value<?>> values = new ArrayList<Value<?>>();
							for (int index = 0; index < numOfColumns; index++) {
								SparkplugBProto.Payload.Metric.DataSet.Value protoValue = protoValues.get(index);
								values.add(convertDataSetValue(protoTypes.get(index), protoValue));
							}

							// Add the values to the row and the row to the rows
							Row row = new Row(values);
							rows.add(row);
						}
					}

					// Add the rows to the DataSet
					dataSet.setRows(rows);
				}

				// Finally set the metric value
				return dataSet;	
			case UDT:
				return null;
			case Unknown:
			default:
				throw new Exception("Failed to decode: Unknown Metric DataType");

		}
	}
	
	private Value<?> convertDataSetValue(SparkplugBProto.Payload.Metric.DataSet.DataType protoType, 
			SparkplugBProto.Payload.Metric.DataSet.Value protoValue) throws Exception {
		
		switch (protoType) {
			case Boolean:
				return new Value<Boolean>(DataSetDataType.Boolean, protoValue.getBooleanValue());
			case DateTime:
				return new Value<Date>(DataSetDataType.DateTime, new Date(protoValue.getLongValue()));
			case Float4:
				return new Value<Float>(DataSetDataType.Float4, protoValue.getFloatValue());
			case Float8:
				return new Value<Double>(DataSetDataType.Float8, protoValue.getDoubleValue());
			case Int1:
				return new Value<Integer>(DataSetDataType.Int1, protoValue.getIntValue());
			case Int2:
				return new Value<Integer>(DataSetDataType.Int2, protoValue.getIntValue());
			case Int4:
				return new Value<Integer>(DataSetDataType.Int4, protoValue.getIntValue());
			case Int8:
				return new Value<Long>(DataSetDataType.Int8, protoValue.getLongValue());
			case String:
				return new Value<String>(DataSetDataType.String, protoValue.getStringValue());
			case Text:
				return new Value<String>(DataSetDataType.Text, protoValue.getStringValue());
			case Unknown:
			default:
			logger.error("Unknown DataType: " + protoType.getValueDescriptor());
			throw new Exception("Failed to decode");	
		}
	}
	
	private DataSetDataType convertValueType(SparkplugBProto.Payload.Metric.DataSet.DataType type) throws Exception {
		switch (type) {
			case Boolean:
				return DataSetDataType.Boolean;
			case DateTime:
				return DataSetDataType.DateTime;
			case Float4:
				return DataSetDataType.Float4;
			case Float8:
				return DataSetDataType.Float8;
			case Int1:
				return DataSetDataType.Int1;
			case Int2:
				return DataSetDataType.Int2;
			case Int4:
				return DataSetDataType.Int4;
			case Int8:
				return DataSetDataType.Int8;
			case String:
				return DataSetDataType.String;
			case Text:
				return DataSetDataType.Text;
			case Unknown:
			default:
				logger.error("Unknown DataType: " + type);
				throw new Exception("Failed to decode");
			
		}
	}
	
	private MetricDataType convertMetricType(SparkplugBProto.Payload.Metric.DataType type) throws Exception {
		switch (type) {
			case Bytes:
				return MetricDataType.Bytes;
			case Dataset:
				return MetricDataType.DataSet;
			case File:
				return MetricDataType.File;
			case UDT:
				return MetricDataType.UDT;
			case Boolean:
				return MetricDataType.Boolean;
			case DateTime:
				return MetricDataType.DateTime;
			case Float4:
				return MetricDataType.Float4;
			case Float8:
				return MetricDataType.Float8;
			case Int1:
				return MetricDataType.Int1;
			case Int2:
				return MetricDataType.Int2;
			case Int4:
				return MetricDataType.Int4;
			case Int8:
				return MetricDataType.Int8;
			case String:
				return MetricDataType.String;
			case Text:
				return MetricDataType.Text;
			case Unknown:
			default:
				logger.error("Unknown DataType: " + type);
				throw new Exception("Failed to decode");
			
		}
	}
}
