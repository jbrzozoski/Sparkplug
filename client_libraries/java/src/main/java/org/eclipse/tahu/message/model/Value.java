/*******************************************************************************
 * Copyright (c) 2014, 2018 Cirrus Link Solutions and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *  Cirrus Link Solutions
 *
 *******************************************************************************/

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
