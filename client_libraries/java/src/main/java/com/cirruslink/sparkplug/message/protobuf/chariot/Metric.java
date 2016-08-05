package com.cirruslink.sparkplug.message.protobuf.chariot;

import java.util.Date;

import com.cirruslink.sparkplug.protobuf.message.ChariotProto.Payload.Metric.DataType;

public class Metric<V> {

	private String name;
	private long alias;
	private Date timestamp;
	private DataType dataType;
	private boolean historical;
	private MetaData metaData;
	
	private V metricValue;
	
	public Metric() {
		super();
	}
	
	public Metric(String name, DataType dataType, V metricValue) {
		super();
		this.name = name;
		this.alias = -1;
		this.timestamp = new Date();
		this.dataType = dataType;
		this.historical = false;
		this.metaData = null;
		this.metricValue = metricValue;
	}

	public Metric(String name, long alias, Date timestamp, DataType dataType, boolean historical, MetaData metaData,
			V metricValue) {
		super();
		this.name = name;
		this.alias = alias;
		this.timestamp = timestamp;
		this.dataType = dataType;
		this.historical = historical;
		this.metaData = metaData;
		this.metricValue = metricValue;
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

	public V getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(V metricValue) {
		this.metricValue = metricValue;
	}

	@Override
	public String toString() {
		return "Metric [name=" + name + ", alias=" + alias + ", timestamp=" + timestamp + ", dataType=" + dataType
				+ ", historical=" + historical + ", metaData=" + metaData + ", metricValue=" + metricValue + "]";
	}
}
