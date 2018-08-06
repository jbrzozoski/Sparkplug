/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.eclipse.tahu;

/**
 * An Exception caused by an invalid type.
 */
public class SparkplugInvalidTypeException extends SparkplugException {

	private Class<?> type;
	
	public SparkplugInvalidTypeException(Class<?> type) {
		super("Invalid type " + type);
		this.type = type;	
	}
}
