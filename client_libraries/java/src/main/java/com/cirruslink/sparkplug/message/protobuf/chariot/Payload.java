package com.cirruslink.sparkplug.message.protobuf.chariot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Payload {

	private Date timestamp;
	private List<Metric<? extends Object>> metrics;
	private long seq = -1;
	private String uuid;
	private byte[] body;
	
	public Payload() {
		super();
		metrics = new ArrayList<Metric<? extends Object>>();
	}

	public Payload(Date timestamp, List<Metric<? extends Object>> metrics, long seq, String uuid, byte[] body) {
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
	
	public void addMetric(Metric<? extends Object> metric) {
		metrics.add(metric);
	}
	
	public void addMetric(int index, Metric<? extends Object> metric) {
		metrics.add(index, metric);
	}
	
	public Metric<? extends Object> removeMetric(int index) {
		return metrics.remove(index);
	}
	
	public boolean removeMetric(Metric<? extends Object> metric) {
		return metrics.remove(metric);
	}

	public List<Metric<? extends Object>> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric<? extends Object>> metrics) {
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
