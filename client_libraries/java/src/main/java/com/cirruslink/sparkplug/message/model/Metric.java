/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.Date;
import java.util.List;

/**
 * A Metric of a Sparkplug Payload.
 */
public class Metric {
	
	private String name;
	private Long alias;
	private Date timestamp;
	private MetricDataType dataType;
	private Boolean isHistorical = null;
	private Boolean isTransient = null;
	private Boolean isNull = null;
	private MetaData metaData;
	private List<Metric> members;
	
	private Object value;
	
	public Metric() {
		super();
	}
	
	public Metric(String name, MetricDataType dataType, Object value) {
		super();
		this.name = name;
		this.alias = null;
		this.timestamp = new Date();
		this.dataType = dataType;
		this.isHistorical = null;
		this.metaData = null;
		this.value = value;
	}

	public Metric(String name, long alias, Date timestamp, MetricDataType dataType, boolean isHistorical, 
			boolean isTransient, boolean isNull, MetaData metaData, Object metricValue, List<Metric> members) {
		super();
		this.name = name;
		this.alias = alias;
		this.timestamp = timestamp;
		this.dataType = dataType;
		this.isHistorical = isHistorical;
		this.isTransient = isTransient;
		this.isNull = isNull;
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
		return (alias == null) ? false : true;
	}

	public Long getAlias() {
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

	public Boolean isHistorical() {
		return isHistorical;
	}

	public void setHistorical(Boolean isHistorical) {
		this.isHistorical = isHistorical;
	}

	public Boolean isTransient() {
		return isTransient;
	}

	public void setTransient(Boolean isTransient) {
		this.isTransient = isTransient;
	}

	public Boolean isNull() {
		return isNull;
	}

	public void setNull(Boolean isNull) {
		this.isNull = isNull;
	}

	@Override
	public String toString() {
		return "Metric [name=" + name + ", alias=" + alias + ", timestamp=" + timestamp + ", dataType=" + dataType
				+ ", historical=" + isHistorical + ", metaData=" + metaData + ", members=" + members + ", value=" + value
				+ "]";
	}
}
