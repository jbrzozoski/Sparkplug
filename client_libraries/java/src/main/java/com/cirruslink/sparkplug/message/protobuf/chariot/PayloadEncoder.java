package com.cirruslink.sparkplug.message.protobuf.chariot;

import java.io.IOException;
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
import com.google.protobuf.ByteString;

public class PayloadEncoder {
	
	private static Logger logger = LogManager.getLogger(PayloadEncoder.class.getName());
	
	public PayloadEncoder() {
		super();
	}
	
	public byte[] getBytes(Payload payload) throws IOException {
		
		ChariotProto.Payload.Builder protoMsg = ChariotProto.Payload.newBuilder();
		
		// Set the timestamp
		if (payload.getTimestamp() != null) {
			logger.debug("Setting time " + payload.getTimestamp());
			protoMsg.setTimestamp(payload.getTimestamp().getTime());
		}
		
		// Set the sequence number
		logger.debug("Setting sequence number " + payload.getSeq());
		protoMsg.setSeq(payload.getSeq());
		
		// Set the UUID
		logger.debug("Setting the UUID " + payload.getUuid());
		protoMsg.setUuid(payload.getUuid());
		
		// Set the metrics
		for (Metric<?> metric : payload.getMetrics()) {
			
			// build a metric
			ChariotProto.Payload.Metric.Builder metricBuilder = ChariotProto.Payload.Metric.newBuilder();
			
			try {
				// set the basic parameters
				logger.debug("Adding metric: " + metric.getName());
				metricBuilder.setName(metric.getName());
				metricBuilder.setAlias(metric.getAlias());
				metricBuilder.setDatatype(metric.getDataType());
				if(metric.getTimestamp() != null) {
					metricBuilder.setTimestamp(metric.getTimestamp().getTime());
				}
				
				// Set the value and metadata
				metricBuilder = setMetricValue(metricBuilder, metric);
				if(metric.getMetaData() != null) {
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
	
	private ChariotProto.Payload.Metric.Builder setMetricValue(ChariotProto.Payload.Metric.Builder metricBuilder,
			Metric<?> metric) throws Exception {
		
		// Set the datatype
		metricBuilder.setDatatype(metric.getDataType());
		
		// Set the value
		if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Unknown)) {
			logger.error("Unknown DataType: " + metric.getDataType());
			throw new Exception("Failed to encode");
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Int1)) {
			metricBuilder.setIntValue((Integer) metric.getMetricValue());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Int2)) {
			metricBuilder.setIntValue((Integer) metric.getMetricValue());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Int4)) {
			metricBuilder.setIntValue((Integer) metric.getMetricValue());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Int8)) {
			metricBuilder.setLongValue((Long) metric.getMetricValue());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Float4)) {
			metricBuilder.setFloatValue((Float) metric.getMetricValue());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Float8)) {
			metricBuilder.setDoubleValue((Double) metric.getMetricValue());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Boolean)) {
			metricBuilder.setBooleanValue((Boolean) metric.getMetricValue());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.String)) {
			metricBuilder.setStringValue((String) metric.getMetricValue());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.DateTime)) {
			metricBuilder.setLongValue(((Date)metric.getMetricValue()).getTime());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Dataset)) {
			DataSet dataSet = (DataSet) metric.getMetricValue();
			ChariotProto.Payload.Metric.DataSet.Builder protoDataSetBuilder = ChariotProto.Payload.Metric.DataSet.newBuilder();
			
			protoDataSetBuilder.setNumOfColumns(dataSet.getNumOfColumns());
			
			List<Value<?>> columns = dataSet.getColumns();
			if(columns != null && !columns.isEmpty()) {
				for(Value<?> column : columns) {
					// Add the converted column
					protoDataSetBuilder.addColumns(convertValue(column));
				}
			} else {
				throw new Exception("Invalid DataSet");
			}
			
			List<Row> rows = dataSet.getRows();
			if(rows != null && !rows.isEmpty()) {
				for(Row row : rows) {
					ChariotProto.Payload.Metric.DataSet.Row.Builder protoRowBuilder = ChariotProto.Payload.Metric.DataSet.Row.newBuilder();
					List<Value<?>> values = row.getValues();
					if(values != null && !values.isEmpty()) {
						for(Value<?> value : values) {
							// Add the converted element
							protoRowBuilder.addElement(convertValue(value));
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
			
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Text)) {
			metricBuilder.setStringValue((String) metric.getMetricValue());
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.Bytes)) {
			metricBuilder.setBytesValue(ByteString.copyFrom((byte[]) metric.getMetricValue()));
		} else if(metric.getDataType().equals(ChariotProto.Payload.Metric.DataType.File)) {
			metricBuilder.setBytesValue(ByteString.copyFrom(((File) metric.getMetricValue()).getBytes()));
			ChariotProto.Payload.Metric.MetaData.Builder metaDataBuilder = ChariotProto.Payload.Metric.MetaData.newBuilder();
			metaDataBuilder.setFileName(((File) metric.getMetricValue()).getFileName());
			metricBuilder.setMetadata(metaDataBuilder);
		} else {
			logger.error("Unknown DataType: " + metric.getDataType());
			throw new Exception("Failed to encode");
		}
		
		return metricBuilder;
	}
	
	private ChariotProto.Payload.Metric.Builder setMetaData(ChariotProto.Payload.Metric.Builder metricBuilder,
			Metric<?> metric) throws Exception {
		
		// If the builder has been built already - use it
		ChariotProto.Payload.Metric.MetaData.Builder metaDataBuilder;
		if(metricBuilder.getMetadataBuilder() != null) {
			metaDataBuilder = metricBuilder.getMetadataBuilder();
		} else {
			metaDataBuilder = ChariotProto.Payload.Metric.MetaData.newBuilder();
		}
		
		MetaData metaData = metric.getMetaData();
		if(metaData.getUnits() != null) {
			metaDataBuilder.setUnits(metaData.getUnits());
		}
		if(metaData.getContentType() != null) {
			metaDataBuilder.setContentType(metaData.getContentType());
		}
		metaDataBuilder.setSize(metaData.getSize());
		if(metaData.getAlgorithm() != null) {
			metaDataBuilder.setAlgorithm(metaData.getAlgorithm());
		}
		if(metaData.getFormat() != null) {
			metaDataBuilder.setFormat(metaData.getFormat());
		}
		metaDataBuilder.setSeq(metaData.getSeq());
		if(metaData.getFileName() != null) {
			metaDataBuilder.setFileName(metaData.getFileName());
		}
		if(metaData.getFileType() != null) {
			metaDataBuilder.setFileType(metaData.getFileType());
		}
		if(metaData.getMd5() != null) {
			metaDataBuilder.setMd5(metaData.getMd5());
		}
		if(metaData.getDescription() != null) {
			metaDataBuilder.setDescription(metaData.getDescription());
		}
		metricBuilder.setMetadata(metaDataBuilder);
		
		return metricBuilder;
	}
	
	private ChariotProto.Payload.Metric.DataSet.Value.Builder convertValue(Value<?> value) throws Exception {
		ChariotProto.Payload.Metric.DataSet.Value.Builder protoValueBuilder = ChariotProto.Payload.Metric.DataSet.Value.newBuilder();
		
		if(value.getType() == ValueDataType.Unknown) {
			logger.error("Unknown DataType: " + value.getType());
			throw new Exception("Failed to convert value " + value.getType());
		} else if(value.getType() == ValueDataType.Int1) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.Int1);
			protoValueBuilder.setIntValue((Integer) value.getValue());
		} else if(value.getType() == ValueDataType.Int2) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.Int2);
			protoValueBuilder.setIntValue((Integer) value.getValue());
		} else if(value.getType() == ValueDataType.Int4) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.Int4);
			protoValueBuilder.setIntValue((Integer) value.getValue());
		} else if(value.getType() == ValueDataType.Int8) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.Int8);
			protoValueBuilder.setLongValue((Long) value.getValue());
		} else if(value.getType() == ValueDataType.Float4) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.Float4);
			protoValueBuilder.setFloatValue((Float) value.getValue());
		} else if(value.getType() == ValueDataType.Float8) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.Float8);
			protoValueBuilder.setDoubleValue((Double) value.getValue());
		} else if(value.getType() == ValueDataType.Boolean) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.Boolean);
			protoValueBuilder.setBooleanValue((Boolean) value.getValue());
		} else if(value.getType() == ValueDataType.String) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.String);
			protoValueBuilder.setStringValue((String) value.getValue());
		} else if(value.getType() == ValueDataType.DateTime) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.DateTime);
			protoValueBuilder.setLongValue(((Date) value.getValue()).getTime());
		} else if(value.getType() == ValueDataType.Text) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.String);
			protoValueBuilder.setStringValue((String) value.getValue());
		} else if(value.getType() == ValueDataType.Null) {
			protoValueBuilder.setType(ChariotProto.Payload.Metric.DataSet.Value.DataType.Null);
		} else {
			logger.error("Unknown DataType: " + value.getType());
			throw new Exception("Failed to convert value " + value.getType());
		}
		
		return protoValueBuilder;
	}
}
