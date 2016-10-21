/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.cirruslink.sparkplug.message.model.DataSet;
import com.cirruslink.sparkplug.message.model.DataSetDataType;
import com.cirruslink.sparkplug.message.model.File;
import com.cirruslink.sparkplug.message.model.MetaData;
import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.MetricDataType;
import com.cirruslink.sparkplug.message.model.Row;
import com.cirruslink.sparkplug.message.model.Value;
import com.cirruslink.sparkplug.message.payload.PayloadDecoder;
import com.cirruslink.sparkplug.message.payload.SparkplugBPayload;
import com.cirruslink.sparkplug.message.payload.SparkplugBPayloadDecoder;
import com.cirruslink.sparkplug.message.payload.SparkplugBPayloadEncoder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SparkplugTest extends TestCase {
	
	static {
	    Logger rootLogger = Logger.getRootLogger();
	    rootLogger.setLevel(Level.ALL);
	    rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%-6r [%p] %c - %m%n")));
	}

	public SparkplugTest(String testName) {
		super( testName );
	}

	public static Test suite() {
		return new TestSuite(SparkplugTest.class);
	}

	public void testEnDeCode() {
		try {
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			sparkplugBPayload.setSeq(0);
			sparkplugBPayload.setBody("Hello".getBytes());
			
			// Create one metric
			Metric metric = new Metric();
			metric.setName("Name");
			metric.setAlias(0);
			metric.setTimestamp(currentTime);
			metric.setDataType(MetricDataType.Int1);
			metric.setHistorical(false);
			metric.setValue(65);
			MetaData metaData = new MetaData();
			metaData.setContentType("none");
			metaData.setSize(12);
			metaData.setSeq(0);
			metaData.setFileName("none");
			metaData.setFileType("none");
			metaData.setMd5("none");
			metaData.setDescription("none");		
			metric.setMetaData(metaData);
			sparkplugBPayload.addMetric(metric);
			
			// Create null metric
			metric = new Metric();
			metric.setName("Null");
			metric.setAlias(0);
			metric.setTimestamp(currentTime);
			metric.setDataType(MetricDataType.String);
			metric.setHistorical(false);
			metric.setValue(null);
			metaData = new MetaData();
			metaData.setContentType("none");
			metaData.setSize(12);
			metaData.setSeq(0);
			metaData.setFileName("none");
			metaData.setFileType("none");
			metaData.setMd5("none");
			metaData.setDescription("none");		
			metric.setMetaData(metaData);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(0, decodedPayload.getSeq());
			assertTrue(Arrays.equals("Hello".getBytes(), decodedPayload.getBody()));
			
			// Metric checks
			assertEquals(2, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("Name", decodedMetric.getName());
			assertEquals(new Long(0), decodedMetric.getAlias());
			assertEquals(currentTime, decodedMetric.getTimestamp());
			assertEquals(MetricDataType.Int1, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65, decodedMetric.getValue());
			assertNotNull(decodedMetric.getMetaData());
			MetaData decodedMetaData = decodedMetric.getMetaData();
			assertEquals("none", decodedMetaData.getContentType());
			assertEquals(12, decodedMetaData.getSize());
			assertEquals(0, decodedMetaData.getSeq());
			assertEquals("none", decodedMetaData.getFileName());
			assertEquals("none", decodedMetaData.getFileType());
			assertEquals("none", decodedMetaData.getMd5());
			assertEquals("none", decodedMetaData.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testPartialPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.Int1, 65);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.Int1, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testInt2Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.Int2, 65);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.Int2, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testInt4Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.Int4, 65);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.Int4, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testInt8Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.Int8, 65L);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.Int8, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65L, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testFloat4Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.Float4, 6.5F);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.Float4, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(6.5F, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testFloat8Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.Float8, 6.5);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.Float8, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(6.5, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testBooleanPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.Boolean, true);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.Boolean, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(true, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testStringPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.String, "MyString");
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.String, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals("MyString", decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testDateTimePayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.DateTime, currentTime);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.DateTime, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(currentTime, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testDatasetPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create the Dataset
			DataSet dataSet = new DataSet();
			dataSet.setNumOfColumns(3);
			
			List<String> columnNames = new ArrayList<String>();
			columnNames.add("Booleans");
			columnNames.add("Int4s");
			columnNames.add("Floats");
			dataSet.setColumnNames(columnNames);
			
			List<DataSetDataType> columnTypes = new ArrayList<DataSetDataType>();
			columnTypes.add(DataSetDataType.Boolean);
			columnTypes.add(DataSetDataType.Int4);
			columnTypes.add(DataSetDataType.Float4);
			dataSet.setTypes(columnTypes);
			
			List<Row> rows = new ArrayList<Row>();
			List<Value<?>> rowValues = new ArrayList<Value<?>>();
			rowValues.add(new Value<Boolean>(DataSetDataType.Boolean, false));
			rowValues.add(new Value<Integer>(DataSetDataType.Int4, 1));
			rowValues.add(new Value<Float>(DataSetDataType.Float4, 1.1F));
			rows.add(new Row(rowValues));
			rowValues = new ArrayList<Value<?>>();
			rowValues.add(new Value<Boolean>(DataSetDataType.Boolean, true));
			rowValues.add(new Value<Integer>(DataSetDataType.Int4, 2));
			rowValues.add(new Value<Float>(DataSetDataType.Float4, 1.2F));
			rows.add(new Row(rowValues));
			rowValues = new ArrayList<Value<?>>();
			rowValues.add(new Value<Boolean>(DataSetDataType.Boolean, false));
			rowValues.add(new Value<Integer>(DataSetDataType.Int4, 3));
			rowValues.add(new Value<Float>(DataSetDataType.Float4, 1.3F));
			rows.add(new Row(rowValues));
			dataSet.setRows(rows);
			
			// Create one metric
			Metric metric = new Metric("MyName", MetricDataType.DataSet, dataSet);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.DataSet, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertNull(decodedMetric.getMetaData());
			
			// DataSet Checks
			DataSet decodedDataSet = (DataSet) decodedMetric.getValue();
			assertEquals(3, decodedDataSet.getNumOfColumns());
			List<String> decodedColumns = decodedDataSet.getColumnNames();
			assertNotNull(decodedColumns);
			assertEquals("Booleans", decodedColumns.get(0));
			assertEquals("Int4s", decodedColumns.get(1));
			assertEquals("Floats", decodedColumns.get(2));
			
			// DataSet row checks
			List<Row> decodedRows = decodedDataSet.getRows();
			assertEquals(3, decodedRows.size());
			Row row1 = decodedRows.get(0);
			List<Value<?>> row1Values = row1.getValues();
			assertEquals(DataSetDataType.Boolean, row1Values.get(0).getType());
			assertEquals(DataSetDataType.Int4, row1Values.get(1).getType());
			assertEquals(DataSetDataType.Float4, row1Values.get(2).getType());
			assertEquals(false, row1Values.get(0).getValue());
			assertEquals(1, row1Values.get(1).getValue());
			assertEquals(1.1F, row1Values.get(2).getValue());

			Row row2 = decodedRows.get(1);
			List<Value<?>> row2Values = row2.getValues();
			assertEquals(DataSetDataType.Boolean, row2Values.get(0).getType());
			assertEquals(DataSetDataType.Int4, row2Values.get(1).getType());
			assertEquals(DataSetDataType.Float4, row2Values.get(2).getType());
			assertEquals(true, row2Values.get(0).getValue());
			assertEquals(2, row2Values.get(1).getValue());
			assertEquals(1.2F, row2Values.get(2).getValue());
			
			Row row3 = decodedRows.get(2);
			List<Value<?>> row3Values = row3.getValues();
			assertEquals(DataSetDataType.Boolean, row3Values.get(0).getType());
			assertEquals(DataSetDataType.Int4, row3Values.get(1).getType());
			assertEquals(DataSetDataType.Float4, row3Values.get(2).getType());
			assertEquals(false, row3Values.get(0).getValue());
			assertEquals(3, row3Values.get(1).getValue());
			assertEquals(1.3F, row3Values.get(2).getValue());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testTextPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyText", MetricDataType.Text, "MyText");
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyText", decodedMetric.getName());
			assertEquals(MetricDataType.Text, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals("MyText", decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testBytesPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			byte[] someBytes = new byte[]{0x0, 0x1, 0x2, 0x3, 0x4};
			Metric metric = new Metric("MyName", MetricDataType.Bytes, someBytes);
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.Bytes, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			
			byte[] decodedBytes = (byte[]) decodedMetric.getValue();
			assertEquals(5, decodedBytes.length);
			assertEquals(someBytes[0], decodedBytes[0]);
			assertEquals(someBytes[1], decodedBytes[1]);
			assertEquals(someBytes[2], decodedBytes[2]);
			assertEquals(someBytes[3], decodedBytes[3]);
			assertEquals(someBytes[4], decodedBytes[4]);
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testFilePayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			byte[] someBytes = new byte[]{0x0, 0x1, 0x2, 0x3, 0x4};
			File file = new File("/tmp/.testfile", someBytes);
			Metric metric = new Metric("MyName", MetricDataType.File, file);
			
			MetaData metaData = new MetaData();
			metaData.setFileType("bin");
			metric.setMetaData(metaData);
			
			sparkplugBPayload.addMetric(metric);
			
			// Encode
			SparkplugBPayloadEncoder encoder = new SparkplugBPayloadEncoder();
			byte[] bytes = encoder.getBytes(sparkplugBPayload);
			
			// Decode
			PayloadDecoder<SparkplugBPayload> decoder = new SparkplugBPayloadDecoder();
			SparkplugBPayload decodedPayload = decoder.buildFromByteArray(bytes);
			
			// SparkplugBPayload checks
			assertEquals(currentTime, decodedPayload.getTimestamp());
			assertEquals(-1, decodedPayload.getSeq());
			assertNull(decodedPayload.getBody());
			
			// Metric checks
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("MyName", decodedMetric.getName());
			assertEquals(MetricDataType.File, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			
			File decodedFile = (File) decodedMetric.getValue();
			assertEquals("/tmp/.testfile", decodedFile.getFileName());
			assertEquals(5, decodedFile.getBytes().length);
			assertEquals(someBytes[0], decodedFile.getBytes()[0]);
			assertEquals(someBytes[1], decodedFile.getBytes()[1]);
			assertEquals(someBytes[2], decodedFile.getBytes()[2]);
			assertEquals(someBytes[3], decodedFile.getBytes()[3]);
			assertEquals(someBytes[4], decodedFile.getBytes()[4]);
			assertNotNull(decodedMetric.getMetaData());
			assertEquals("/tmp/.testfile", decodedMetric.getMetaData().getFileName());
			assertEquals(false, decodedMetric.getMetaData().isMultiPart());
			assertEquals(false, decodedMetric.getMetaData().isScript());
			assertEquals("bin", decodedMetric.getMetaData().getFileType());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
