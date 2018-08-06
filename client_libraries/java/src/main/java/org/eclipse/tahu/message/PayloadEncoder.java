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
