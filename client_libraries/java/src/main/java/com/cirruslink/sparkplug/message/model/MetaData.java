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
	private String contentType;
	private long size;
	private long seq;

	// File metadata
	private String fileName;
	private String fileType;
	private String md5;
	
	// Indicates to remove a tag
	private boolean remove;
	
	// UDT metadata
	private String typeId;
	
	// Catchall for future expansion
	private String description;

	public MetaData() {
		super();
	}

	public MetaData(boolean isMultiPart, boolean isScript, String contentType, long size, long seq, String fileName, 
			String fileType, String md5, boolean remove, String typeId, String description) {
		super();
		this.isMultiPart = isMultiPart;
		this.isScript = isScript;
		this.contentType = contentType;
		this.size = size;
		this.seq = seq;
		this.fileName = fileName;
		this.fileType = fileType;
		this.md5 = md5;
		this.remove = remove;
		this.typeId = typeId;
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
	
	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}
	
	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "MetaData [isMultiPart=" + isMultiPart + ", isScript=" + isScript + ", contentType=" + contentType 
				+ ", size=" + size + ", seq=" + seq + ", fileName=" + fileName + ", fileType=" + fileType + ", md5=" 
				+ md5 + ", description=" + description + "]";
	}
}
