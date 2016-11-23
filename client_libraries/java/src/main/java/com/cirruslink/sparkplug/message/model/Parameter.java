/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.util.Objects;

import com.cirruslink.sparkplug.SparkplugInvalidTypeException;

/**
 * A class to represent a parameter associated with a template.
 */
public class Parameter {
	
	/**
	 * The name of the parameter
	 */
	private String name;
	
	/**
	 * The data type of the parameter
	 */
	private ParameterDataType type;
	
	/**
	 * The value of the parameter
	 */
	private Object value;
	
	
	/**
	 * Constructs a Parameter instance.
	 * 
	 * @param name The name of the parameter.
	 * @param type The type of the parameter.
	 * @param value The value of the parameter.
	 * @throws SparkplugInvalidTypeException 
	 */
	public Parameter(String name, ParameterDataType type, Object value) throws SparkplugInvalidTypeException {
		this.name = name;
		this.type = type;
		this.value = value;
		this.type.checkType(value);
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
	
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || this.getClass() != object.getClass()) {
			return false;
		}
		Parameter param = (Parameter) object;
		return Objects.equals(name, param.getName())
				&& Objects.equals(type, param.getType())
				&& Objects.equals(value, param.getValue());
	}
}
