/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.eclipse.tahu.message.model;

public class Value<V> {
	
	private DataSetDataType type;
	private V value;
	
	public Value() {
		super();
	}

	public Value(DataSetDataType type, V value) {
		super();
		this.type = type;
		this.value = value;
	}

	public DataSetDataType getType() {
		return type;
	}

	public void setType(DataSetDataType type) {
		this.type = type;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Value [type=" + type + ", value=" + value + "]";
	}
}
