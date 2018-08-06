/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.eclipse.tahu;

/**
 * An Exception thrown if an error is encountered while parsing a payload or topic. 
 */
public class SparkplugParsingException extends SparkplugException {

	/**
	 * Constructor 
	 * 
	 * @param message an error message
	 */
	public SparkplugParsingException(String message) {
		super(message);
	}

	/**
	 * Constructor 
	 * 
	 * @param message an error message
	 * @param exception an underlying exception
	 */
	public SparkplugParsingException(String message, Throwable exception) {
		super(message, exception);
	}

}
