/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message;

/**
 * An interface for decoding payloads.
 * 
 * @param <P> the type of payload.
 */
public interface PayloadDecoder <P> {

	/**
	 * Builds a payload from a supplied byte array.
	 * 
	 * @param bytes the bytes representing the payload
	 * @return a payload object built from the byte array 
	 * @throws Exception
	 */
	public P buildFromByteArray(byte[] bytes) throws Exception;
}
