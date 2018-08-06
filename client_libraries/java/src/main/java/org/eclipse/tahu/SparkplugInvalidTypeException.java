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
