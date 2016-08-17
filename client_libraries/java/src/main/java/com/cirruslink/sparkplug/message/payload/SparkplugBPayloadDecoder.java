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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cirruslink.sparkplug.message.model.DataSet;
import com.cirruslink.sparkplug.message.model.File;
import com.cirruslink.sparkplug.message.model.MetaData;
import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.Row;
import com.cirruslink.sparkplug.message.model.Value;
import com.cirruslink.sparkplug.message.model.ValueDataType;
import com.cirruslink.sparkplug.message.protobuf.SparkplugBProto;

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

			Metric metric = new Metric();
			SparkplugBProto.Payload.Metric.DataType dataType = protoMetric.getDatatype();
			
			if(dataType == SparkplugBProto.Payload.Metric.DataType.Unknown) {
				throw new Exception("Failed to decode");				
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.Int1 || dataType == SparkplugBProto.Payload.Metric.DataType.Int2 || dataType == SparkplugBProto.Payload.Metric.DataType.Int4) {
				metric.setValue(protoMetric.getIntValue());
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.Int8) {
				metric.setValue(protoMetric.getLongValue());
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.Float4) {
				metric.setValue(protoMetric.getFloatValue());
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.Float8) {
				metric.setValue(protoMetric.getDoubleValue());
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.Boolean) {
				metric.setValue(protoMetric.getBooleanValue());
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.String) {
				metric.setValue(protoMetric.getStringValue());
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.DateTime) {
				metric.setValue(new Date(protoMetric.getLongValue()));
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.Dataset) {
				DataSet dataSet = new DataSet();
				SparkplugBProto.Payload.Metric.DataSet protoDataSet = protoMetric.getDatasetValue();
				dataSet.setNumOfColumns(protoDataSet.getNumOfColumns());
				
				// Set the columns
				if(protoDataSet.getColumnsCount() > 0) {
					List<SparkplugBProto.Payload.Metric.DataSet.Value> protoColumns = protoDataSet.getColumnsList();
					List<Value<?>> columns = new ArrayList<Value<?>>();
					for(SparkplugBProto.Payload.Metric.DataSet.Value protoColumn : protoColumns) {
						columns.add(convertValue(protoColumn));
					}

					// Finally set the columns
					dataSet.setColumns(columns);
				}
				
				if(protoDataSet.getRowsCount() > 0) {
					List<SparkplugBProto.Payload.Metric.DataSet.Row> protoRows = protoDataSet.getRowsList();
					List<Row> rows = new ArrayList<Row>();
					for(SparkplugBProto.Payload.Metric.DataSet.Row protoRow : protoRows) {
						if(protoRow.getElementCount() > 0) {
							List<SparkplugBProto.Payload.Metric.DataSet.Value> protoValues = protoRow.getElementList();
							List<Value<?>> values = new ArrayList<Value<?>>();
							for(SparkplugBProto.Payload.Metric.DataSet.Value protoValue : protoValues) {
								values.add(convertValue(protoValue));
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
				metric.setValue(dataSet);				
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.Text) {
				metric.setValue(protoMetric.getStringValue());
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.Bytes) {
				metric.setValue(protoMetric.getBytesValue().toByteArray());
			} else if(dataType == SparkplugBProto.Payload.Metric.DataType.File) {
				metric.setValue(new File(protoMetric.getMetadata().getFileName(), protoMetric.getBytesValue().toByteArray()));
			} else {
				throw new Exception("Failed to decode");
			}
			
			// Set the other tag data
			metric.setName(protoMetric.getName());
			metric.setAlias(protoMetric.getAlias());
			metric.setTimestamp(new Date(protoMetric.getTimestamp()));
			metric.setDataType(protoMetric.getDatatype());
			metric.setHistorical(protoMetric.getHistorical());
			
			// Set the metadata
			if(protoMetric.hasMetadata()) {
				logger.debug("Metadata is not null");
				SparkplugBProto.Payload.Metric.MetaData protoMetaData = protoMetric.getMetadata();
				MetaData metaData = new MetaData();
				metaData.setUnits(protoMetaData.getUnits());
				metaData.setContentType(protoMetaData.getContentType());
				metaData.setSize(protoMetaData.getSize());
				metaData.setAlgorithm(protoMetaData.getAlgorithm());
				metaData.setFormat(protoMetaData.getFormat());
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
	
	private Value<?> convertValue(SparkplugBProto.Payload.Metric.DataSet.Value protoValue) throws Exception {
		SparkplugBProto.Payload.Metric.DataSet.Value.DataType protoType = protoValue.getType();
		if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Unknown) {
			logger.error("Unknown DataType: " + protoType.getValueDescriptor());
			throw new Exception("Failed to decode");
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Int1) {
			Value<Integer> value = new Value<Integer>(ValueDataType.Int1, protoValue.getIntValue());
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Int2) {
			Value<Integer> value = new Value<Integer>(ValueDataType.Int2, protoValue.getIntValue());
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Int4) {
			Value<Integer> value = new Value<Integer>(ValueDataType.Int4, protoValue.getIntValue());
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Int8) {
			Value<Long> value = new Value<Long>(ValueDataType.Int8, protoValue.getLongValue());
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Float4) {
			Value<Float> value = new Value<Float>(ValueDataType.Float4, protoValue.getFloatValue());
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Float8) {
			Value<Double> value = new Value<Double>(ValueDataType.Float8, protoValue.getDoubleValue());
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Boolean) {
			Value<Boolean> value = new Value<Boolean>(ValueDataType.Boolean, protoValue.getBooleanValue());
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.String) {
			Value<String> value = new Value<String>(ValueDataType.String, protoValue.getStringValue());
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.DateTime) {
			Value<Date> value = new Value<Date>(ValueDataType.DateTime, new Date(protoValue.getLongValue()));
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Text) {
			Value<String> value = new Value<String>(ValueDataType.Text, protoValue.getStringValue());
			return value;
		} else if(protoType == SparkplugBProto.Payload.Metric.DataSet.Value.DataType.Null) {
			Value<Integer> value = new Value<Integer>(ValueDataType.Null, null);
			return value;
		} else {
			logger.error("Unknown DataType: " + protoType.getValueDescriptor());
			throw new Exception("Failed to decode");
		}

	}
}
