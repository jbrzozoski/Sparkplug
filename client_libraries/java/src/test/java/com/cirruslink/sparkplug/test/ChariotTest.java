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
import com.cirruslink.sparkplug.message.model.File;
import com.cirruslink.sparkplug.message.model.MetaData;
import com.cirruslink.sparkplug.message.model.Metric;
import com.cirruslink.sparkplug.message.model.Row;
import com.cirruslink.sparkplug.message.model.Value;
import com.cirruslink.sparkplug.message.model.ValueDataType;
import com.cirruslink.sparkplug.message.payload.PayloadDecoder;
import com.cirruslink.sparkplug.message.payload.SparkplugBPayload;
import com.cirruslink.sparkplug.message.payload.SparkplugBPayloadDecoder;
import com.cirruslink.sparkplug.message.payload.SparkplugBPayloadEncoder;
import com.cirruslink.sparkplug.message.protobuf.SparkplugBProto.Payload.Metric.DataType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ChariotTest extends TestCase {
	
	static {
	    Logger rootLogger = Logger.getRootLogger();
	    rootLogger.setLevel(Level.ALL);
	    rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%-6r [%p] %c - %m%n")));
	}

	public ChariotTest(String testName) {
		super( testName );
	}

	public static Test suite() {
		return new TestSuite(ChariotTest.class);
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
			metric.setDataType(DataType.Int1);
			metric.setHistorical(false);
			metric.setValue(65);
			MetaData metaData = new MetaData();
			metaData.setUnits("mph");
			metaData.setContentType("none");
			metaData.setSize(12);
			metaData.setAlgorithm("none");
			metaData.setFormat("none");
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
			assertEquals(1, decodedPayload.getMetrics().size());
			Metric decodedMetric = decodedPayload.getMetrics().get(0);
			assertEquals("Name", decodedMetric.getName());
			assertEquals(0, decodedMetric.getAlias());
			assertEquals(currentTime, decodedMetric.getTimestamp());
			assertEquals(DataType.Int1, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65, decodedMetric.getValue());
			assertNotNull(decodedMetric.getMetaData());
			MetaData decodedMetaData = decodedMetric.getMetaData();
			assertEquals("mph", decodedMetaData.getUnits());
			assertEquals("none", decodedMetaData.getContentType());
			assertEquals(12, decodedMetaData.getSize());
			assertEquals("none", decodedMetaData.getAlgorithm());
			assertEquals("none", decodedMetaData.getFormat());
			assertEquals(0, decodedMetaData.getSeq());
			assertEquals("none", decodedMetaData.getFileName());
			assertEquals("none", decodedMetaData.getFileType());
			assertEquals("none", decodedMetaData.getMd5());
			assertEquals("none", decodedMetaData.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testPartialPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.Int1, 65);
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
			assertEquals(DataType.Int1, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testInt2Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.Int2, 65);
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
			assertEquals(DataType.Int2, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testInt4Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.Int4, 65);
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
			assertEquals(DataType.Int4, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testInt8Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.Int8, 65L);
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
			assertEquals(DataType.Int8, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(65L, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testFloat4Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.Float4, 6.5F);
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
			assertEquals(DataType.Float4, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(6.5F, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testFloat8Payload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.Float8, 6.5);
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
			assertEquals(DataType.Float8, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(6.5, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testBooleanPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.Boolean, true);
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
			assertEquals(DataType.Boolean, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(true, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testStringPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.String, "MyString");
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
			assertEquals(DataType.String, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals("MyString", decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testDateTimePayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.DateTime, currentTime);
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
			assertEquals(DataType.DateTime, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals(currentTime, decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
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
			List<Value<?>> columns = new ArrayList<Value<?>>();
			columns.add(new Value<String>(ValueDataType.String, "Booleans"));
			columns.add(new Value<String>(ValueDataType.String, "Int4s"));
			columns.add(new Value<String>(ValueDataType.String, "Floats"));
			dataSet.setColumns(columns);
			List<Row> rows = new ArrayList<Row>();
			List<Value<?>> rowValues = new ArrayList<Value<?>>();
			rowValues.add(new Value<Boolean>(ValueDataType.Boolean, false));
			rowValues.add(new Value<Integer>(ValueDataType.Int4, 1));
			rowValues.add(new Value<Float>(ValueDataType.Float4, 1.1F));
			rows.add(new Row(rowValues));
			rowValues = new ArrayList<Value<?>>();
			rowValues.add(new Value<Boolean>(ValueDataType.Boolean, true));
			rowValues.add(new Value<Integer>(ValueDataType.Int4, 2));
			rowValues.add(new Value<Float>(ValueDataType.Float4, 1.2F));
			rows.add(new Row(rowValues));
			rowValues = new ArrayList<Value<?>>();
			rowValues.add(new Value<Boolean>(ValueDataType.Boolean, false));
			rowValues.add(new Value<Integer>(ValueDataType.Int4, 3));
			rowValues.add(new Value<Float>(ValueDataType.Float4, 1.3F));
			rows.add(new Row(rowValues));
			dataSet.setRows(rows);
			
			// Create one metric
			Metric metric = new Metric("MyName", DataType.Dataset, dataSet);
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
			assertEquals(DataType.Dataset, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertNull(decodedMetric.getMetaData());
			
			// DataSet Checks
			DataSet decodedDataSet = (DataSet) decodedMetric.getValue();
			assertEquals(3, decodedDataSet.getNumOfColumns());
			List<Value<?>> decodedColumns = decodedDataSet.getColumns();
			assertNotNull(decodedColumns);
			assertEquals(ValueDataType.String, decodedColumns.get(0).getType());
			assertEquals(ValueDataType.String, decodedColumns.get(1).getType());
			assertEquals(ValueDataType.String, decodedColumns.get(2).getType());
			assertEquals("Booleans", decodedColumns.get(0).getValue());
			assertEquals("Int4s", decodedColumns.get(1).getValue());
			assertEquals("Floats", decodedColumns.get(2).getValue());
			
			// DataSet row checks
			List<Row> decodedRows = decodedDataSet.getRows();
			assertEquals(3, decodedRows.size());
			Row row1 = decodedRows.get(0);
			List<Value<?>> row1Values = row1.getValues();
			assertEquals(ValueDataType.Boolean, row1Values.get(0).getType());
			assertEquals(ValueDataType.Int4, row1Values.get(1).getType());
			assertEquals(ValueDataType.Float4, row1Values.get(2).getType());
			assertEquals(false, row1Values.get(0).getValue());
			assertEquals(1, row1Values.get(1).getValue());
			assertEquals(1.1F, row1Values.get(2).getValue());

			Row row2 = decodedRows.get(1);
			List<Value<?>> row2Values = row2.getValues();
			assertEquals(ValueDataType.Boolean, row2Values.get(0).getType());
			assertEquals(ValueDataType.Int4, row2Values.get(1).getType());
			assertEquals(ValueDataType.Float4, row2Values.get(2).getType());
			assertEquals(true, row2Values.get(0).getValue());
			assertEquals(2, row2Values.get(1).getValue());
			assertEquals(1.2F, row2Values.get(2).getValue());
			
			Row row3 = decodedRows.get(2);
			List<Value<?>> row3Values = row3.getValues();
			assertEquals(ValueDataType.Boolean, row3Values.get(0).getType());
			assertEquals(ValueDataType.Int4, row3Values.get(1).getType());
			assertEquals(ValueDataType.Float4, row3Values.get(2).getType());
			assertEquals(false, row3Values.get(0).getValue());
			assertEquals(3, row3Values.get(1).getValue());
			assertEquals(1.3F, row3Values.get(2).getValue());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testTextPayload() {
		try {
			// Now test a minimal payload
			Date currentTime = new Date();
			SparkplugBPayload sparkplugBPayload = new SparkplugBPayload();
			sparkplugBPayload.setTimestamp(currentTime);
			
			// Create one metric
			Metric metric = new Metric("MyText", DataType.Text, "MyText");
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
			assertEquals(DataType.Text, decodedMetric.getDataType());
			assertEquals(false, decodedMetric.isHistorical());
			assertEquals("MyText", decodedMetric.getValue());
			assertNull(decodedMetric.getMetaData());
			
		} catch (Exception e) {
			e.printStackTrace();
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
			Metric metric = new Metric("MyName", DataType.Bytes, someBytes);
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
			assertEquals(DataType.Bytes, decodedMetric.getDataType());
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
			Metric metric = new Metric("MyName", DataType.File, file);
			
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
			assertEquals(DataType.File, decodedMetric.getDataType());
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
		}
	}
}
