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

package org.eclipse.tahu.message;

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
