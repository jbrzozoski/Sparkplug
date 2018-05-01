/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016-2018 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

import com.cirruslink.sparkplug.SparkplugParsingException;

/**
 * An enumeration of Sparkplug MQTT message types.  The type provides an indication as to what the MQTT Payload of 
 * message will contain.
 */
public enum MessageType {
	
	/**
	 * Birth certificate for MQTT Edge of Network (EoN) Nodes.
	 */
	NBIRTH,
	
	/**
	 * Death certificate for MQTT Edge of Network (EoN) Nodes.
	 */
	NDEATH,
	
	/**
	 * Birth certificate for MQTT Devices.
	 */
	DBIRTH,
	
	/**
	 * Death certificate for MQTT Devices.
	 */
	DDEATH,
	
	/**
	 * Edge of Network (EoN) Node data message.
	 */
	NDATA,
	
	/**
	 * Device data message.
	 */
	DDATA,
	
	/**
	 * Edge of Network (EoN) Node command message.
	 */
	NCMD,
	
	/**
	 * Device command message.
	 */
	DCMD,
	
	/**
	 * Critical application state message.
	 */
	STATE,
	
	/**
	 * Device record message.
	 */
	DRECORD,
	
	/**
	 * Edge of Network (EoN) Node record message.
	 */
	NRECORD;
	
	public static MessageType parseMessageType(String type) throws SparkplugParsingException {
		for (MessageType messageType : MessageType.values()) {
			if (messageType.name().equals(type)) {
				return messageType;
			}
		}
		throw new SparkplugParsingException("Invalid message type: " + type);
	}
	
	public boolean isDeath() {
		return this.equals(DDEATH) || this.equals(NDEATH);
	}
	
	public boolean isCommand() {
		return this.equals(DCMD) || this.equals(NCMD);
	}
	
	public boolean isData() {
		return this.equals(DDATA) || this.equals(NDATA);
	}
	
	public boolean isBirth() {
		return this.equals(DBIRTH) || this.equals(NBIRTH);
	}
	
	public boolean isRecord() {
		return this.equals(DRECORD) || this.equals(NRECORD);
	}
}
