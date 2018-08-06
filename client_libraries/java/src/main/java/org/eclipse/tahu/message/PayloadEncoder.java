/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.eclipse.tahu.message;

import java.io.IOException;

/**
 * An interface for encoding payloads.
 * 
 * @param <P> the type of payload.
 */
public interface PayloadEncoder <P> {

	/**
	 * Converts a payload object into a byte array.
	 * 
	 * @param payload a payload object
	 * @return the byte array representing the payload 
	 * @throws IOException
	 */
	public byte[] getBytes(P payload) throws IOException;
}
