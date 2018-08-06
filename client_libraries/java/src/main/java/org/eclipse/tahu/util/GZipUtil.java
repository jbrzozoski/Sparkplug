/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2018 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package org.eclipse.tahu.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtil {

	public static byte[] decompress(byte[] compressedData) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
		GZIPInputStream gis = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] decompressedData = null;
		int bufferSize = 1024;

		try {
			gis = new GZIPInputStream(bais);
			byte[] buffer = new byte[bufferSize];
			int bytesRead = -1;
			while ((bytesRead = gis.read(buffer, 0, bufferSize)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}

			baos.flush();
			decompressedData = baos.toByteArray();
		} finally {
			if (gis != null) {
				gis.close();
			}
			if (bais != null) {
				bais.close();
			}
			if (baos != null) {
				baos.close();
			}
		}
		return decompressedData;
	}

	public static byte[] compress(byte[] uncompressedData) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gos = null;
		byte[] compressedData = null;

		try {
			gos = new GZIPOutputStream(baos);
			gos.write(uncompressedData, 0, uncompressedData.length);
			gos.finish();
			gos.flush();
			baos.flush();
			compressedData = baos.toByteArray();
		} finally {
			if (gos != null) {
				gos.close();
			}
			if (baos != null) {
				baos.close();
			}
		}
		return compressedData;
	}
}
