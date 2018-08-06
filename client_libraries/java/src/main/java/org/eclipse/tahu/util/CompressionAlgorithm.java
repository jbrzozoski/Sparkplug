/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2017 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

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
