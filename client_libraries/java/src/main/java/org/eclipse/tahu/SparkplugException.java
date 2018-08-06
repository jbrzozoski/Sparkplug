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
 * A Sparkplug Exception
 */
public class SparkplugException extends Exception {

	/**
	 * Default constructor.
	 */
	public SparkplugException() {};
	
	/**
	 * Constructor 
	 * 
	 * @param message an error message
	 */
	public SparkplugException(String message) {
		super(message);
	}

	/**
	 * Constructor 
	 * 
	 * @param message an error message
	 * @param exception an underlying exception
	 */
	public SparkplugException(String message, Throwable exception) {
		super(message, exception);
	}

}
