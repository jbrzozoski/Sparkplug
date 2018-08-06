/*******************************************************************************
 * Copyright (c) 2017, 2018 Cirrus Link Solutions and/or its affiliates and others
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

package org.eclipse.tahu.util;

/**
 * An enumeration of supported payload compression algorithms
 */
public enum CompressionAlgorithm {

	GZIP,
	DEFLATE;
	
	public static CompressionAlgorithm parse(String algorithm) {
		return CompressionAlgorithm.valueOf(algorithm.toUpperCase());
	}
}
