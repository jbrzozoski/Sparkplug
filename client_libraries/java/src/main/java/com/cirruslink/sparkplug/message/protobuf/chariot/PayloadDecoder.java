package com.cirruslink.sparkplug.message.protobuf.chariot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cirruslink.sparkplug.message.protobuf.chariot.types.DataSet;
import com.cirruslink.sparkplug.message.protobuf.chariot.types.File;
import com.cirruslink.sparkplug.message.protobuf.chariot.types.Row;
import com.cirruslink.sparkplug.message.protobuf.chariot.types.Value;
import com.cirruslink.sparkplug.message.protobuf.chariot.types.ValueDataType;
import com.cirruslink.sparkplug.protobuf.message.ChariotProto;

public class PayloadDecoder {
	
	private static Logger logger = LogManager.getLogger(PayloadDecoder.class.getName());

	public PayloadDecoder() {
		super();
	}
	
	public Payload buildFromByteArray(byte[] bytes) throws Exception {
		ChariotProto.Payload protoPayload = ChariotProto.Payload.parseFrom(bytes);
		Payload payload = new Payload();
		
		// Set the timestamp
		if (protoPayload.hasTimestamp()) {
			logger.debug("Setting time " + new Date(protoPayload.getTimestamp()));
			payload.setTimestamp(new Date(protoPayload.getTimestamp()));
		}
		
		// Set the sequence number
		if (protoPayload.hasSeq()) {
			logger.debug("Setting sequence number " + protoPayload.getSeq());
			payload.setSeq(protoPayload.getSeq());
		}
		
		// Set the Metrics
		for (int i = 0; i < protoPayload.getMetricCount(); i++) {
		
			// Get the Metric from protobuf
			ChariotProto.Payload.Metric protoMetric = protoPayload.getMetric(i);

			Metric metric = null;
			ChariotProto.Payload.Metric.DataType dataType = protoMetric.getDatatype();
			
			if(dataType == ChariotProto.Payload.Metric.DataType.Unknown) {
				throw new Exception("Failed to decode");				
			} else if(dataType == ChariotProto.Payload.Metric.DataType.Int1 || dataType == ChariotProto.Payload.Metric.DataType.Int2 || dataType == ChariotProto.Payload.Metric.DataType.Int4) {
				metric = new Metric<Integer>();
				metric.setMetricValue(protoMetric.getIntValue());
			} else if(dataType == ChariotProto.Payload.Metric.DataType.Int8) {
				metric = new Metric<Long>();
				metric.setMetricValue(protoMetric.getLongValue());
			} else if(dataType == ChariotProto.Payload.Metric.DataType.Float4) {
				metric = new Metric<Float>();
				metric.setMetricValue(protoMetric.getFloatValue());
			} else if(dataType == ChariotProto.Payload.Metric.DataType.Float8) {
				metric = new Metric<Double>();
				metric.setMetricValue(protoMetric.getDoubleValue());
			} else if(dataType == ChariotProto.Payload.Metric.DataType.Boolean) {
				metric = new Metric<Boolean>();
				metric.setMetricValue(protoMetric.getBooleanValue());
			} else if(dataType == ChariotProto.Payload.Metric.DataType.String) {
				metric = new Metric<String>();
				metric.setMetricValue(protoMetric.getStringValue());
			} else if(dataType == ChariotProto.Payload.Metric.DataType.DateTime) {
				metric = new Metric<Date>();
				metric.setMetricValue(new Date(protoMetric.getLongValue()));
			} else if(dataType == ChariotProto.Payload.Metric.DataType.Dataset) {
				metric = new Metric<DataSet>();
				DataSet dataSet = new DataSet();
				ChariotProto.Payload.Metric.DataSet protoDataSet = protoMetric.getDatasetValue();
				dataSet.setNumOfColumns(protoDataSet.getNumOfColumns());
				
				// Set the columns
				if(protoDataSet.getColumnsCount() > 0) {
					List<ChariotProto.Payload.Metric.DataSet.Value> protoColumns = protoDataSet.getColumnsList();
					List<Value<?>> columns = new ArrayList<Value<?>>();
					for(ChariotProto.Payload.Metric.DataSet.Value protoColumn : protoColumns) {
						columns.add(convertValue(protoColumn));
					}

					// Finally set the columns
					dataSet.setColumns(columns);
				}
				
				if(protoDataSet.getRowsCount() > 0) {
					List<ChariotProto.Payload.Metric.DataSet.Row> protoRows = protoDataSet.getRowsList();
					List<Row> rows = new ArrayList<Row>();
					for(ChariotProto.Payload.Metric.DataSet.Row protoRow : protoRows) {
						if(protoRow.getElementCount() > 0) {
							List<ChariotProto.Payload.Metric.DataSet.Value> protoValues = protoRow.getElementList();
							List<Value<?>> values = new ArrayList<Value<?>>();
							for(ChariotProto.Payload.Metric.DataSet.Value protoValue : protoValues) {
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
				metric.setMetricValue(dataSet);				
			} else if(dataType == ChariotProto.Payload.Metric.DataType.Text) {
				metric = new Metric<String>();
				metric.setMetricValue(protoMetric.getStringValue());
			} else if(dataType == ChariotProto.Payload.Metric.DataType.Bytes) {
				metric = new Metric<byte[]>();
				metric.setMetricValue(protoMetric.getBytesValue().toByteArray());
			} else if(dataType == ChariotProto.Payload.Metric.DataType.File) {
				metric = new Metric<File>();
				metric.setMetricValue(new File(protoMetric.getMetadata().getFileName(), protoMetric.getBytesValue().toByteArray()));
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
				ChariotProto.Payload.Metric.MetaData protoMetaData = protoMetric.getMetadata();
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
			
			payload.addMetric(metric);
		}
		
		// Set the body
		if (protoPayload.hasBody()) {
			logger.debug("Setting the body " + new String(protoPayload.getBody().toByteArray()));
			payload.setBody(protoPayload.getBody().toByteArray());
		}
		
		return payload;
	}
	
	private Value<?> convertValue(ChariotProto.Payload.Metric.DataSet.Value protoValue) throws Exception {
		ChariotProto.Payload.Metric.DataSet.Value.DataType protoType = protoValue.getType();
		if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Unknown) {
			logger.error("Unknown DataType: " + protoType.getValueDescriptor());
			throw new Exception("Failed to decode");
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Int1) {
			Value<Integer> value = new Value<Integer>(ValueDataType.Int1, protoValue.getIntValue());
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Int2) {
			Value<Integer> value = new Value<Integer>(ValueDataType.Int2, protoValue.getIntValue());
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Int4) {
			Value<Integer> value = new Value<Integer>(ValueDataType.Int4, protoValue.getIntValue());
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Int8) {
			Value<Long> value = new Value<Long>(ValueDataType.Int8, protoValue.getLongValue());
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Float4) {
			Value<Float> value = new Value<Float>(ValueDataType.Float4, protoValue.getFloatValue());
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Float8) {
			Value<Double> value = new Value<Double>(ValueDataType.Float8, protoValue.getDoubleValue());
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Boolean) {
			Value<Boolean> value = new Value<Boolean>(ValueDataType.Boolean, protoValue.getBooleanValue());
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.String) {
			Value<String> value = new Value<String>(ValueDataType.String, protoValue.getStringValue());
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.DateTime) {
			Value<Date> value = new Value<Date>(ValueDataType.DateTime, new Date(protoValue.getLongValue()));
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Text) {
			Value<String> value = new Value<String>(ValueDataType.Text, protoValue.getStringValue());
			return value;
		} else if(protoType == ChariotProto.Payload.Metric.DataSet.Value.DataType.Null) {
			Value<Integer> value = new Value<Integer>(ValueDataType.Null, null);
			return value;
		} else {
			logger.error("Unknown DataType: " + protoType.getValueDescriptor());
			throw new Exception("Failed to decode");
		}

	}
}
