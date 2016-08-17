/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2016 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.message.payload;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cirruslink.sparkplug.message.model.Metric;

public class SparkplugBPayload {

	private Date timestamp;
	private List<Metric> metrics;
	private long seq = -1;
	private String uuid;
	private byte[] body;
	
	public SparkplugBPayload() {
		super();
		metrics = new ArrayList<Metric>();
	}

	public SparkplugBPayload(Date timestamp, List<Metric> metrics, long seq, String uuid, byte[] body) {
		super();
		this.timestamp = timestamp;
		this.metrics = metrics;
		this.seq = seq;
		this.uuid = uuid;
		this.body = body;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public void addMetric(Metric metric) {
		metrics.add(metric);
	}
	
	public void addMetric(int index, Metric metric) {
		metrics.add(index, metric);
	}
	
	public Metric removeMetric(int index) {
		return metrics.remove(index);
	}
	
	public boolean removeMetric(Metric metric) {
		return metrics.remove(metric);
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public long getSeq() {
		return seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
}
