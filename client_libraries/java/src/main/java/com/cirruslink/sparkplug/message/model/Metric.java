/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.Date;

import com.cirruslink.sparkplug.message.protobuf.SparkplugBProto.Payload.Metric.DataType;

public class Metric {
	
	public static final DataType DATATYPE_BOOLEAN = DataType.Boolean;
	public static final DataType DATATYPE_INT1 = DataType.Int1;
	public static final DataType DATATYPE_INT2 = DataType.Int2;
	public static final DataType DATATYPE_INT4 = DataType.Int4;
	public static final DataType DATATYPE_INT8 = DataType.Int8;
	public static final DataType DATATYPE_FLOAT4 = DataType.Float4;
	public static final DataType DATATYPE_FLOAT8 = DataType.Float8;
	public static final DataType DATATYPE_STRING = DataType.String;
	public static final DataType DATATYPE_TEXT = DataType.Text;
	public static final DataType DATATYPE_DATETIME = DataType.DateTime;
	public static final DataType DATATYPE_UDT_INST = DataType.UdtInst;
	public static final DataType DATATYPE_UDT_DEF = DataType.UdtDef;
	public static final DataType DATATYPE_DATASET = DataType.Dataset;
	public static final DataType DATATYPE_BYTES = DataType.Bytes;
	public static final DataType DATATYPE_FILE = DataType.File;
	public static final DataType DATATYPE_UNKNOWN = DataType.Unknown;

	private String name;
	private long alias;
	private Date timestamp;
	private DataType dataType;
	private boolean historical;
	private MetaData metaData;
	
	private Object value;
	
	public Metric() {
		super();
	}
	
	public Metric(String name, DataType dataType, Object value) {
		super();
		this.name = name;
		this.alias = -1;
		this.timestamp = new Date();
		this.dataType = dataType;
		this.historical = false;
		this.metaData = null;
		this.value = value;
	}

	public Metric(String name, long alias, Date timestamp, DataType dataType, boolean historical, MetaData metaData,
			Object metricValue) {
		super();
		this.name = name;
		this.alias = alias;
		this.timestamp = timestamp;
		this.dataType = dataType;
		this.historical = historical;
		this.metaData = metaData;
		this.value = metricValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAlias() {
		return alias;
	}

	public void setAlias(long alias) {
		this.alias = alias;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public boolean isHistorical() {
		return historical;
	}

	public void setHistorical(boolean historical) {
		this.historical = historical;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public boolean hasAlias() {
		return alias != -1;
	}

	@Override
	public String toString() {
		return "Metric [name=" + name + ", alias=" + alias + ", timestamp=" + timestamp + ", dataType=" + dataType
				+ ", historical=" + historical + ", metaData=" + metaData + ", metricValue=" + value + "]";
	}
}
