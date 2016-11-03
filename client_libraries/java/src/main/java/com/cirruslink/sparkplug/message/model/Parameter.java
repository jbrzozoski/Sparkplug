/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

/**
 * A parameter of a template.
 */
public class Parameter {
	
	private String name;
	private ParameterDataType type;
	private Object value;
	
	/**
	 * @param name
	 * @param type
	 * @param value
	 */
	public Parameter(String name, ParameterDataType type, Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ParameterDataType getType() {
		return type;
	}

	public void setType(ParameterDataType type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
