/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.Date;

import com.cirruslink.sparkplug.SparkplugInvalidTypeException;

/**
 * A metric of a Sparkplug Payload.
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
	private PropertySet propertySet;
	private Object value;

	/**
	 * @param name
	 * @param alias
	 * @param timestamp
	 * @param dataType
	 * @param isHistorical
	 * @param isTransient
	 * @param isNull
	 * @param metaData
	 * @param propertySet
	 * @param value
	 * @throws SparkplugInvalidTypeException 
	 */
	public Metric(String name, Long alias, Date timestamp, MetricDataType dataType, Boolean isHistorical,
			Boolean isTransient, MetaData metaData, PropertySet propertySet, Object value) 
					throws SparkplugInvalidTypeException {
		super();
		this.name = name;
		this.alias = alias;
		this.timestamp = timestamp;
		this.dataType = dataType;
		this.isHistorical = isHistorical;
		this.isTransient = isTransient;
		isNull = (value == null) ? true : false;
		this.metaData = metaData;
		this.propertySet = propertySet;
		this.value = value;
		this.dataType.checkType(value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasName() {
		return !(name == null);
	}
	
	public boolean hasAlias() {
		return !(alias == null);
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
		isNull = (value == null);
	}

	public PropertySet getPropertySet() {
		return this.propertySet;
	}

	public void setPropertySet(PropertySet propertySet) {
		this.propertySet = propertySet;
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
	
	@Override
	public String toString() {
		return "Metric [name=" + name + ", alias=" + alias + ", timestamp=" + timestamp + ", dataType=" + dataType
				+ ", isHistorical=" + isHistorical + ", isTransient=" + isTransient + ", isNull=" + isNull
				+ ", metaData=" + metaData + ", propertySet=" + propertySet + ", value=" + value + "]";
	}	
	
	/**
	 * A builder for creating a {@link Metric} instance.
	 */
	public static class MetricBuilder {

		private String name;
		private Long alias;
		private Date timestamp;
		private MetricDataType dataType;
		private Boolean isHistorical = null;
		private Boolean isTransient = null;
		private MetaData metaData = null;
		private PropertySet propertySet = null;
		private Object value;
		
		public MetricBuilder(String name, MetricDataType dataType, Object value) {
			this.name = name;
			this.timestamp = new Date();
			this.dataType = dataType;
			this.value = value;
		}
		
		public MetricBuilder(Long alias, MetricDataType dataType, Object value) {
			this.alias = alias;
			this.timestamp = new Date();
			this.dataType = dataType;
			this.value = value;
		}

		public MetricBuilder name(String name) {
			this.name = name;
			return this;
		}

		public MetricBuilder alias(Long alias) {
			this.alias = alias;
			return this;
		}

		public MetricBuilder timestamp(Date timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public MetricBuilder dataType(MetricDataType dataType) {
			this.dataType = dataType;
			return this;
		}

		public MetricBuilder isHistorical(Boolean isHistorical) {
			this.isHistorical = isHistorical;
			return this;
		}

		public MetricBuilder isTransient(Boolean isTransient) {
			this.isTransient = isTransient;
			return this;
		}

		public MetricBuilder metaData(MetaData metaData) {
			this.metaData = metaData;
			return this;
		}

		public MetricBuilder propertySet(PropertySet propertySet) {
			this.propertySet = propertySet;
			return this;
		}

		public MetricBuilder value(Object value) {
			this.value = value;
			return this;
		}
		
		public Metric createMetric() throws SparkplugInvalidTypeException {
			return new Metric(name, alias, timestamp, dataType, isHistorical, isTransient, metaData, 
					propertySet, value);
		}
	}
}
