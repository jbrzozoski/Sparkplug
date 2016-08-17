/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.model;

public class MetaData {

	// Bytes metadata
	private boolean isMultiPart;
	private boolean isScript;
	
	// General metadata
	private String units;
	private String contentType;
	private long size;
	private String algorithm;
	private String format;
	private long seq;

	// File metadata
	private String fileName;
	private String fileType;
	private String md5;
	
	// Catchall for future expansion
	private String description;

	public MetaData() {
		super();
	}

	public MetaData(boolean isMultiPart, boolean isScript, String units, String contentType, long size,
			String algorithm, String format, long seq, String fileName, String fileType, String md5,
			String description) {
		super();
		this.isMultiPart = isMultiPart;
		this.isScript = isScript;
		this.units = units;
		this.contentType = contentType;
		this.size = size;
		this.algorithm = algorithm;
		this.format = format;
		this.seq = seq;
		this.fileName = fileName;
		this.fileType = fileType;
		this.md5 = md5;
		this.description = description;
	}

	public boolean isMultiPart() {
		return isMultiPart;
	}

	public void setMultiPart(boolean isMultiPart) {
		this.isMultiPart = isMultiPart;
	}

	public boolean isScript() {
		return isScript;
	}

	public void setScript(boolean isScript) {
		this.isScript = isScript;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public long getSeq() {
		return seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "MetaData [isMultiPart=" + isMultiPart + ", isScript=" + isScript + ", units=" + units + ", contentType="
				+ contentType + ", size=" + size + ", algorithm=" + algorithm + ", format=" + format + ", seq=" + seq
				+ ", fileName=" + fileName + ", fileType=" + fileType + ", md5=" + md5 + ", description=" + description
				+ "]";
	}
}
