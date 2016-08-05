package com.cirruslink.sparkplug.message.protobuf.chariot.types;

import java.util.Arrays;

public class File {
	
	private String fileName;
	private byte[] bytes;
	
	public File() {
		super();
	}
	
	public File(String fileName, byte[] bytes) {
		super();
		this.fileName = fileName.replace("/", System.getProperty("file.separator"))
				.replace("\\", System.getProperty("file.separator"));
		this.bytes = Arrays.copyOf(bytes, bytes.length);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
