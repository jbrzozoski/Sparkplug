/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.Objects;

import com.cirruslink.sparkplug.SparkplugInvalidTypeException;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The value of a property in a {@link PropertySet}.
 */
public class PropertyValue {
	
	private PropertyDataType type;
	private Object value;
	private Boolean isNull = null;
	
	public PropertyValue() {}
	
	/**
	 * A constructor.
	 * 
	 * @param type the property type
	 * @param value the property value
	 * @throws SparkplugInvalidTypeException 
	 */
	public PropertyValue(PropertyDataType type, Object value) throws SparkplugInvalidTypeException {
		this.type = type;
		this.value = value;
		isNull = (value == null) ? true : false;
		type.checkType(value);
	}

	public PropertyDataType getType() {
		return type;
	}

	public void setType(PropertyDataType type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		isNull = (value == null) ? true : false;
	}
	
	@JsonIgnore
	public Boolean isNull() {
		return isNull;
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || this.getClass() != object.getClass()) {
			return false;
		}
		PropertyValue propValue = (PropertyValue) object;
		return Objects.equals(type, propValue.getType())
				&& Objects.equals(value, propValue.getValue());
	}

	@Override
	public String toString() {
		return "PropertyValue [type=" + type + ", value=" + value + ", isNull=" + isNull + "]";
	}
}
