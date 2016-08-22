/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

/**
 * An enumeration of data types associated with a Metric
 */
public enum MetricDataType {
	Unknown,
	Int1,
	Int2,
	Int4,
	Int8,
	Float4,
	Float8,
	Boolean,
	String,
	DateTime,
	DataSet,
	Text,
	UdtInst,
	UdtDef,
	Bytes,
	File
}
