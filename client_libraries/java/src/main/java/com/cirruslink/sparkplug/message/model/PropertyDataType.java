/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import java.awt.List;
import java.math.BigInteger;
import java.util.Date;

import com.cirruslink.sparkplug.SparkplugInvalidTypeException;

/**
 * An enumeration of data types for property values
 */
public enum PropertyDataType {
	
	// Basic Types
	Int8(1, Byte.class),
	Int16(2, Short.class),
	Int32(3, Integer.class),
	Int64(4, Long.class),
	UInt8(5, Short.class),
	UInt16(6, Integer.class),
	UInt32(7, Long.class),
	UInt64(8, BigInteger.class),
	Float(9, Float.class),
	Double(10, Double.class),
	Boolean(11, Boolean.class),
	String(12, String.class),
	DateTime(13, Date.class),
	Text(14, String.class),
	
	// Custom Types for PropertySets
	PropertySet(20, PropertySet.class),
	PropertySetList(21, List.class),
	
	// Unknown
	Unknown(0, Object.class);
	
	private Class<?> clazz = null;
	private int intValue = 0;
	
	private PropertyDataType(int intValue, Class<?> clazz) {
		this.intValue = intValue;
		this.clazz = clazz;
	}
	
	public void checkType(Object value) throws SparkplugInvalidTypeException {
		if (value != null && !value.getClass().equals(clazz)) {
			throw new SparkplugInvalidTypeException(value.getClass());
		}
	}
	
	public int toIntValue() {
		return this.intValue;
	}
	
	public static PropertyDataType fromInteger(int i) {
		switch(i) {
			case 1:
				return Int8;
			case 2:
				return Int16;
			case 3:
				return Int32;
			case 4:
				return Int64;
			case 5:
				return UInt8;
			case 6:
				return UInt16;
			case 7:
				return UInt32;
			case 8:
				return UInt64;
			case 9:
				return Float;
			case 10:
				return Double;
			case 11:
				return Boolean;
			case 12:
				return String;
			case 13:
				return DateTime;
			case 14:
				return Text;
			case 20:
				return PropertySet;
			case 21:
				return PropertySetList;
			default:
				return Unknown;
		}
	}
}
