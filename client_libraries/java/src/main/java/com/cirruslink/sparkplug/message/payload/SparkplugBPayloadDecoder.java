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
			
			if (protoMetric.hasAlias()) {
				metric.setAlias(protoMetric.getAlias());
			}

			metric.setHistorical(protoMetric.getIsHistorical());

			metric.setHistorical(protoMetric.getIsTransient());

			metric.setHistorical(protoMetric.getIsNull());

			// Set the timestamp
			metric.setTimestamp(new Date(protoMetric.getTimestamp()));

			// Get and convert the metric data type
			metric.setDataType(MetricDataType.fromInteger((protoMetric.getDatatype())));
			
			// Set the metadata
			if (protoMetric.hasMetadata()) {
				logger.debug("Metadata is not null");
				SparkplugBProto.Payload.MetaData protoMetaData = protoMetric.getMetadata();
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
		if (protoMetric.getIsNull()) {
			return null;
		}
		// Otherwise convert the value based on the type
		switch (MetricDataType.fromInteger(protoMetric.getDatatype())) {
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
			case Int16:
			case Int32:
			case UInt8:
			case UInt16:
			case UInt32:
				return protoMetric.getIntValue();
			case Int64:
			case UInt64:
				return protoMetric.getLongValue();
			case String:
			case Text:
			case UUID:
				return protoMetric.getStringValue();
			case Bytes:
				return protoMetric.getBytesValue().toByteArray();
			case DataSet:
				DataSet dataSet = new DataSet();
				SparkplugBProto.Payload.DataSet protoDataSet = protoMetric.getDatasetValue();
				List<String> protoColumns = protoDataSet.getColumnsList();
				List<Integer> protoTypes;
				List<SparkplugBProto.Payload.DataSet.Row> protoRows = protoDataSet.getRowsList();
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
					for (int type : protoTypes) {
						types.add(DataSetDataType.fromInteger(type));
					}

					// Set the column types
					dataSet.setTypes(types);
				} else {
					throw new Exception("Failed to decode: DataSet must have types");
				}

				// Set the rows
				if (protoDataSet.getRowsCount() > 0) {
					List<Row> rows = new ArrayList<Row>();
					for (SparkplugBProto.Payload.DataSet.Row protoRow : protoRows) {
						if (protoRow.getElementsCount() > 0) {
							List<SparkplugBProto.Payload.DataSet.DataSetValue> protoValues = protoRow.getElementsList();
							List<Value<?>> values = new ArrayList<Value<?>>();
							for (int index = 0; index < numOfColumns; index++) {
								SparkplugBProto.Payload.DataSet.DataSetValue protoValue = protoValues.get(index);
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
			case Template:
				return null;
			case Unknown:
			default:
				throw new Exception("Failed to decode: Unknown Metric DataType");

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
			case UInt8:
				return new Value<Integer>(type, protoValue.getIntValue());
			case Int16:
			case UInt16:
				return new Value<Integer>(type, protoValue.getIntValue());
			case Int32:
			case UInt32:
				return new Value<Integer>(type, protoValue.getIntValue());
			case Int64:
			case UInt64:
				return new Value<Long>(type, protoValue.getLongValue());
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
