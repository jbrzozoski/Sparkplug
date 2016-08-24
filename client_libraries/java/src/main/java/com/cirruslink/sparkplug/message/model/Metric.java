/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.Date;
import java.util.List;

public class Metric {

	private String name;
	private long alias;
	private Date timestamp;
	private MetricDataType dataType;
	private boolean historical;
	private MetaData metaData;
	private List<Metric> members;
	
	private Object value;
	
	public Metric() {
		super();
	}
	
	public Metric(String name, MetricDataType dataType, Object value) {
		super();
		this.name = name;
		this.alias = -1;
		this.timestamp = new Date();
		this.dataType = dataType;
		this.historical = false;
		this.metaData = null;
		this.value = value;
	}

	public Metric(String name, long alias, Date timestamp, MetricDataType dataType, boolean historical, MetaData metaData,
			Object metricValue, List<Metric> members) {
		super();
		this.name = name;
		this.alias = alias;
		this.timestamp = timestamp;
		this.dataType = dataType;
		this.historical = historical;
		this.metaData = metaData;
		this.value = metricValue;
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasAlias() {
		return alias != -1;
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

	public MetricDataType getDataType() {
		return dataType;
	}

	public void setDataType(MetricDataType dataType) {
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
	
	public List<Metric> getMembers() {
		return members;
	}

	public void setMembers(List<Metric> members) {
		this.members = members;
	}

	@Override
	public String toString() {
		return "Metric [name=" + name + ", alias=" + alias + ", timestamp=" + timestamp + ", dataType=" + dataType
				+ ", historical=" + historical + ", metaData=" + metaData + ", members=" + members + ", value=" + value
				+ "]";
	}
}
