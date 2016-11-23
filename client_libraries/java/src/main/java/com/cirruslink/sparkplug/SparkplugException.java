/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug;

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
