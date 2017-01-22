/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public enum DataQuality {
	
	OPC_BAD_DATA(0),
	OPC_CONFIG_ERROR(4),
	OPC_NOT_CONNECTED(8),
	OPC_DEVICE_FAILURE(12),
	OPC_SENSOR_FAILURE(16),
	OPC_BAD_SHOWING_LAST(20),
	OPC_COMM_FAIL(24),
	OPC_OUT_OF_SERVICE(28),
	OPC_WAITING(32),
	OPC_UNCERTAIN(64),
	OPC_UNCERTAIN_SHOWING_LAST(68),
	OPC_SENSOR_BAD(80),
	OPC_LIMIT_EXCEEDED(84),
	OPC_SUB_NORMAL(88),
	OPC_UNKNOWN(256),
	GOOD_DATA(192),
	OPC_GOOD_WITH_LOCAL_OVERRIDE(216),
	CONFIG_ERROR(300),
	COMM_ERROR(301),
	EXPRESSION_EVAL_ERROR(310),
	SQL_QUERY_ERROR(311),
	DB_CONN_ERROR(312),
	TAG_EXEC_ERROR(330),
	TYPE_CONVERSION_ERROR(340),
	ACCESS_DENIED(403),
	NOT_FOUND(404),
	DISABLED(410),
	STALE(500),
	UNKNOWN(600),
	WRITE_PENDING(700),
	DEMO_EXPIRED(900),
	GW_COMM_OFF(901),
	TAG_LIMIT_EXCEEDED(902),
	GOOD_PROVISIONAL(320),
	REFERENCE_NOT_FOUND(405),
	AGGREGATE_NOT_FOUND(1000);

	private static Logger logger = LogManager.getLogger(DataQuality.class.getName());

	private int intValue = 0;

	private DataQuality(int intValue) {
		this.intValue = intValue;
	}

	/**
	 * Returns an integer representation of the data type.
	 * 
	 * @return an integer representation of the data type.
	 */
	public int toIntValue() {
		return this.intValue;
	}

	/**
	 * Converts the integer representation of the data type into a {@link DataQuality} instance.
	 * 
	 * @param i the integer representation of the data type.
	 * @return a {@link DataQuality} instance.
	 */
	public static DataQuality fromInteger(int i) {
		switch(i) {
			case 0:
				return OPC_BAD_DATA;
			case 4:
				return OPC_CONFIG_ERROR;
			case 8:
				return OPC_NOT_CONNECTED;
			case 12:
				return OPC_DEVICE_FAILURE;
			case 16:
				return OPC_SENSOR_FAILURE;
			case 20:
				return OPC_BAD_SHOWING_LAST;
			case 24:
				return OPC_COMM_FAIL;
			case 28:
				return OPC_OUT_OF_SERVICE;
			case 32:
				return OPC_WAITING;
			case 64:
				return OPC_UNCERTAIN;
			case 68:
				return OPC_UNCERTAIN_SHOWING_LAST;
			case 80:
				return OPC_SENSOR_BAD;
			case 84:
				return OPC_LIMIT_EXCEEDED;
			case 88:
				return OPC_SUB_NORMAL;
			case 256:
				return OPC_UNKNOWN;
			case 192:
				return GOOD_DATA;
			case 216:
				return OPC_GOOD_WITH_LOCAL_OVERRIDE;
			case 300:
				return CONFIG_ERROR;
			case 301:
				return COMM_ERROR;
			case 310:
				return EXPRESSION_EVAL_ERROR;
			case 311:
				return SQL_QUERY_ERROR;
			case 312:
				return DB_CONN_ERROR;
			case 330:
				return TAG_EXEC_ERROR;
			case 340:
				return TYPE_CONVERSION_ERROR;
			case 403:
				return ACCESS_DENIED;
			case 404:
				return NOT_FOUND;
			case 410:
				return DISABLED;
			case 500:
				return STALE;
			case 600:
				return UNKNOWN;
			case 700:
				return WRITE_PENDING;
			case 900:
				return DEMO_EXPIRED;
			case 901:
				return GW_COMM_OFF;
			case 902:
				return TAG_LIMIT_EXCEEDED;
			case 320:
				return GOOD_PROVISIONAL;
			case 405:
				return REFERENCE_NOT_FOUND;
			case 1000:
				return AGGREGATE_NOT_FOUND;
			default:
				return UNKNOWN;
			}
	}
}
