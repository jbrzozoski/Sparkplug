/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

/**
 * The value of a property in a Property Set.
 */
public class PropertyValue {
	
	private PropertyDataType type;
	private Object value;
	
	/**
	 * @param type
	 * @param value
	 */
	public PropertyValue(PropertyDataType type, Object value) {
		super();
		this.type = type;
		this.value = value;
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
	}
	

}
